package wagon.ssh.demo;

import java.io.File;

public interface SshShell extends java.io.Closeable {

    /**
     * Execute a command and return its output
     *
     * @param command
     * @throws ShellException
     * @return
     */
    ShellOutputs execute(String command);

    /**
     * Update local file to remote host and execute
     *
     * @param command
     * @throws ShellException
     * @return
     */
    ShellOutputs execute(File file);

    /**
     * Update local file to remote host and execute
     *
     * @param command
     * @throws ShellException
     * @return
     */
    ShellOutputs execute(File file, String args);


    /**
     * Upload a file
     *
     * @param from
     * @param to
     * @throws ShellException
     */
    void upload(File from, String to);

    /**
     * Download a file
     *
     * @param from
     * @param to
     * @throws ShellException
     */
    void download(String from, File to);

    /**
     * Terminate the shell. Same as close()
     */
    void quit();

}
