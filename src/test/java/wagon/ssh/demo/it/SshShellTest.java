package wagon.ssh.demo.it;

import org.junit.Before;
import org.junit.Test;

import wagon.ssh.demo.SshShell;
import wagon.ssh.demo.SshShellFactory;
import wagon.ssh.demo.internal.SshShellFactoryImpl;

public class SshShellTest {

    private SshShellFactory sshShellFactory;

    @Before
    public void beforeTest() {
        this.sshShellFactory = new SshShellFactoryImpl();
    }

    @Test
    public void testDowload() throws Exception {

        String testHost = "localhost";

        SshShell sshShell = this.sshShellFactory.getShell(testHost, "root", "changeme");

        //sshShell.download(from, to);


    }

}
