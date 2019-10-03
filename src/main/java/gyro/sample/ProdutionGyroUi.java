package gyro.sample;

import gyro.core.GyroUI;
import org.fusesource.jansi.AnsiRenderer;

public class ProdutionGyroUi implements GyroUI {

    private GyroUI delegate;

    public ProdutionGyroUi(GyroUI delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isVerbose() {
        return delegate.isVerbose();
    }

    @Override
    public void setVerbose(boolean b) {
        delegate.setVerbose(b);
    }

    @Override
    public boolean readBoolean(Boolean aBoolean, String s, Object... objects) {
        s = AnsiRenderer.render("[PRODUCTION] " + s, AnsiRenderer.Code.FG_RED.toString());
        return delegate.readBoolean(aBoolean, s, objects);
    }

    @Override
    public void readEnter(String s, Object... objects) {
        delegate.readEnter(s, objects);
    }

    @Override
    public <E extends Enum<E>> E readNamedOption(E e) {
        return delegate.readNamedOption(e);
    }

    @Override
    public String readPassword(String s, Object... objects) {
        return delegate.readPassword(s, objects);
    }

    @Override
    public String readText(String s, Object... objects) {
        return delegate.readText(s, objects);
    }

    @Override
    public void indent() {
        delegate.indent();
    }

    @Override
    public void unindent() {
        delegate.unindent();
    }

    @Override
    public void write(String s, Object... objects) {
        delegate.write(s, objects);
    }

    @Override
    public void replace(String s, Object... objects) {
        delegate.replace(s, objects);
    }

}
