package wagon.ssh.demo;

public interface SshShellFactory {

    /**
     * Create cacheable SshShell using password authentication
     *
     * @param host
     * @param user
     * @param password when empty implicit private key kicks in
     * @return
     */
    SshShell getShell(String host, String user, String password);

    /**
     * Create a SshShell using private key
     *
     * @param host
     * @param user
     * @return
     */
    SshShell getShell(String host, String user);

}
