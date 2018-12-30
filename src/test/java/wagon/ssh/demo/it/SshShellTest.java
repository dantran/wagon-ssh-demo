package wagon.ssh.demo.it;

import java.io.File;

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
    public void testSshShell() throws Exception {

        String testHost = "localhost";

        SshShell sshShell = this.sshShellFactory.getShell(testHost, "build", "changeme");

        String commonRemoteFile = "/tmp/wagon-ssh-demo.dat";
        File commonDownloadFile = new File("target/wagon-ssh-demo.dat");
        commonDownloadFile.getParentFile().mkdirs();

        File smallLocalFile = new File("target/pom.xml");
        sshShell.upload(smallLocalFile, commonRemoteFile);
        sshShell.download(commonRemoteFile, commonDownloadFile);  //wagon-3.3 hangs here


        File largeLocalFile = new File("src/test/data/32k-plus.txt");
        sshShell.upload(largeLocalFile, commonRemoteFile);
        sshShell.download(commonRemoteFile, commonDownloadFile);

        sshShell.execute("rm -f " + commonRemoteFile);

    }

}
