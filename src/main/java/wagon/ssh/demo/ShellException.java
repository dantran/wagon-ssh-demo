package wagon.ssh.demo;

public class ShellException
    extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ShellException( String message ) {
        super( message );
    }

    public ShellException( String message, Throwable e ) {
        super( message, e );
    }

    public ShellException( Throwable e ) {
        super( e );
    }
}
