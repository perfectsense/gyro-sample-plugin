package gyro.sample;

import gyro.core.Type;
import gyro.core.reference.ReferenceResolver;
import gyro.core.scope.Scope;

import java.util.List;

@Type("gyro-sample")
public class GyroSampleResolver extends ReferenceResolver {
    @Override
    public Object resolve(Scope scope, List<Object> arguments) throws Exception {
        return null;
    }
}
