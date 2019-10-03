package gyro.sample.directive;

import gyro.core.GyroCore;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.directive.DirectiveProcessor;
import gyro.core.scope.RootScope;
import gyro.lang.ast.block.DirectiveNode;
import gyro.sample.ProdutionGyroUi;

@Type("production")
public class ProductionProtectionDirectiveProcessor extends DirectiveProcessor<RootScope> {

    @Override
    public void process(RootScope rootScope, DirectiveNode directiveNode) throws Exception {
        GyroUI ui = GyroCore.popUi();
        if (!(ui instanceof ProdutionGyroUi)) {
            ui = new ProdutionGyroUi(ui);
        }

        GyroCore.pushUi(ui);
    }
}
