package gyro.sample.command;

import java.util.List;
import java.util.stream.Collectors;

import gyro.aws.AwsCredentials;
import gyro.aws.autoscaling.AutoScalingGroupResource;
import gyro.aws.ec2.InstanceResource;
import gyro.core.GyroCore;
import gyro.core.command.AbstractConfigCommand;
import gyro.core.resource.Resource;
import gyro.core.scope.RootScope;
import gyro.core.scope.State;
import io.airlift.airline.Command;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.InstanceStateName;

@Command(name = "down", description = "Stop AWS instances and clear instances in autoscaling groups.")
public class DownCommand extends AbstractConfigCommand {

    @Override
    public void doExecute(RootScope current, RootScope pending, State state) {
        List<Resource> resources = current.findResourcesIn(current.getLoadFiles());
        List<InstanceResource> instances = resources
            .stream()
            .filter(InstanceResource.class::isInstance)
            .map(InstanceResource.class::cast)
            .filter(this::isInstanceRunning)
            .collect(Collectors.toList());

        List<AutoScalingGroupResource> autoscalingGroups = resources
            .stream()
            .filter(AutoScalingGroupResource.class::isInstance)
            .map(AutoScalingGroupResource.class::cast)
            .filter(this::isAutoScalingGroupActive)
            .collect(Collectors.toList());

        if (instances.isEmpty() && autoscalingGroups.isEmpty()) {
            GyroCore.ui().write("\nNo running instances or auto scaling groups found, beam down terminate.\n");
            return;
        }

        if (!instances.isEmpty()) {
            GyroCore.ui().write("\nInstances:\n");
            instances.forEach(this::displayInstance);
        }

        if (!autoscalingGroups.isEmpty()) {
            GyroCore.ui().write("\nAuto scaling groups:\n");
            autoscalingGroups.forEach(this::displayAutoScalingGroup);
        }

        if (GyroCore.ui()
            .readBoolean(
                Boolean.FALSE,
                "\nAre you sure you want to stop instances and clear the auto scaling groups listed above?")) {
            instances.forEach(this::stopInstance);
            autoscalingGroups.forEach(this::clearAutoscalingGroup);
        }
    }

    private boolean isInstanceRunning(InstanceResource instance) {
        return InstanceStateName.RUNNING.toString().equals(instance.getGyroInstanceState());
    }

    private void displayInstance(InstanceResource instance) {
        GyroCore.ui().write("- [%s] %s\n", instance.getGyroInstanceLocation(), instance.getGyroInstanceId());
    }

    private void stopInstance(InstanceResource instance) {
        try (Ec2Client client = InstanceResource.createClient(
            Ec2Client.class,
            instance.credentials(AwsCredentials.class))) {
            client.stopInstances(r -> r.instanceIds(instance.getGyroInstanceId()));
        }
    }

    private boolean isAutoScalingGroupActive(AutoScalingGroupResource group) {
        return group.getMinSize() > 0 || group.getMaxSize() > 0;
    }

    private void displayAutoScalingGroup(AutoScalingGroupResource group) {
        GyroCore.ui().write("- %s\n", group.getName());
    }

    private void clearAutoscalingGroup(AutoScalingGroupResource group) {
        try (AutoScalingClient client = AutoScalingGroupResource.createClient(
            AutoScalingClient.class,
            group.credentials(AwsCredentials.class))) {
            client.updateAutoScalingGroup(r -> r
                .autoScalingGroupName(group.getName())
                .maxSize(0)
                .minSize(0));
        }
    }

}
