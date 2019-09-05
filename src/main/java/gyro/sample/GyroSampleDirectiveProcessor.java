package gyro.sample;

import gyro.core.Type;
import gyro.core.directive.DirectiveProcessor;
import gyro.core.scope.Scope;
import gyro.lang.ast.block.DirectiveNode;

@Type("sample")
public class GyroSampleDirectiveProcessor extends DirectiveProcessor {
    @Override
    public void process(Scope scope, DirectiveNode node) throws Exception {

    }
}
