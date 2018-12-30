package wagon.ssh.demo.internal;

import java.io.File;
import java.io.IOException;

import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.Streams;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.providers.ssh.jsch.ScpWagon;

import wagon.ssh.demo.ShellException;
import wagon.ssh.demo.ShellOutputs;
import wagon.ssh.demo.SshShell;


public class SshShellImpl implements SshShell {

    private final ScpWagon wagon;

    private final SshShellFactoryImpl factory;

    public SshShellImpl(ScpWagon wagon, SshShellFactoryImpl factory) {
        this.wagon = wagon;
        this.factory = factory;
    }

    @Override
    public ShellOutputs execute(String command) {
        try {
            Streams streams = wagon.executeCommand(command, true, false);
            return new ShellOutputs(streams.getOut(), streams.getErr());
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

    @Override
    public ShellOutputs execute(File localFile) {
    	return this.execute(localFile, null);
    }

    @Override
    public ShellOutputs execute(File localFile, String args) {
        try {
        	String remoteFile = "/tmp/" + localFile.getName();
        	this.upload(localFile, remoteFile);
        	String cmd = "chmod +x " + remoteFile + ";" + remoteFile;
        	if ( args != null ) {
        		cmd += " " + args;
        	}
        	ShellOutputs output = this.execute(cmd);
        	this.execute("rm -f " + remoteFile);
        	return output;
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }
    @Override
    public void quit() {
        //safe repeated closeConnection()
        wagon.closeConnection();
    }

    @Override
    public void upload(File from, String to) {
        try {
            wagon.put(from, to);
        } catch (TransferFailedException | ResourceDoesNotExistException | AuthorizationException e) {
            throw new ShellException("Unable to upload file: " + from, e);
        }
    }

    @Override
    public void download(String from, File to) {
        try {
            wagon.get(from, to);
        } catch (TransferFailedException | ResourceDoesNotExistException | AuthorizationException e) {
            throw new ShellException("Unable to download file: " + from, e);
        }
    }

    @Override
    public void close() throws IOException {
        factory.removeShell(wagon.getRepository().getHost());
        quit();
    }


    @Override
    public String toString() {
        return "DefaultSshShell [wagon=" + wagon.getAuthenticationInfo() + "]";
    }

}
