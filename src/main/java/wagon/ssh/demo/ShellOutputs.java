package wagon.ssh.demo;

public class ShellOutputs  {

    public final String out;

    public final String err;

    public ShellOutputs(String out, String err) {
        this.out = out == null ? "" : out;
        this.err = err == null ? "" : err;
    }

    @Override
    public String toString() {
        return out + ":" + err;
    }

}