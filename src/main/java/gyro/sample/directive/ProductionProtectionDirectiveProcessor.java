package gyro.sample.directive;

import gyro.core.GyroCore;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.directive.DirectiveProcessor;
import gyro.core.scope.FileScope;
import gyro.lang.ast.block.DirectiveNode;
import gyro.sample.ProdutionGyroUi;

@Type("production")
public class ProductionProtectionDirectiveProcessor extends DirectiveProcessor<FileScope> {

    @Override
    public void process(FileScope fileScope, DirectiveNode directiveNode) throws Exception {
        GyroUI ui = GyroCore.popUi();
        if (!(ui instanceof ProdutionGyroUi)) {
            ui = new ProdutionGyroUi(ui);
        }

        GyroCore.pushUi(ui);
    }
}
