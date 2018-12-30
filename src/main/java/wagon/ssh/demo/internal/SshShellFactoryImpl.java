package wagon.ssh.demo.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.observers.Debug;
import org.apache.maven.wagon.providers.ssh.jsch.ScpWagon;
import org.apache.maven.wagon.providers.ssh.knownhost.KnownHostsProvider;
import org.apache.maven.wagon.providers.ssh.knownhost.NullKnownHostProvider;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;

import wagon.ssh.demo.ShellException;
import wagon.ssh.demo.SshShell;
import wagon.ssh.demo.SshShellFactory;

@Named("sshShellFactory")
@Lazy
public class SshShellFactoryImpl implements SshShellFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int SSH_DEFAULT_PORT = 22;

    private boolean enableSessionLog = true;

    /**
     * Internal cache using hostname as lookup key
     */
    private final Map<String, SshShell> cache = new ConcurrentHashMap<>();

    public void setEnableSessionLog(boolean enableSessionLog) {
        this.enableSessionLog = enableSessionLog;
    }

    // /////////////////////////////////////////////////////////////////////

    @Override
    public SshShell getShell(String host, String user, String password) {

        return this.getShell(host, SSH_DEFAULT_PORT, user, password);
    }

    @Override
    public SshShell getShell(String host, String user) {

        return this.getShell(host, SSH_DEFAULT_PORT, user, null);
    }

    @PreDestroy
    public void cleanup() {

        this.logger.info("{}", "Closing all existing SshShell");

        Collection<SshShell> shells = this.cache.values();

        for (SshShell shell : shells) {
            try {
                shell.close();
            } catch (IOException e) {
                logger.warn("Unable to close {}", shell.toString(), e);
            }
        }

    }

    public void removeShell(String host) {
        this.cache.remove(host);
    }

    // /////////////////////////////////////////////////////////////////////

    private SshShell getShell(String host, int port, String username, String password) {

        SshShell shell = cache.get(host);
        if (shell != null) {
            return shell;
        }

        shell = create(host, port, username, password);
        cache.put(host, shell);

        return shell;
    }

    private SshShell create(String host, int port, String username, String password) {

        File privateKey = new File(System.getProperty("user.home"), ".ssh/id_dsa");
        if (!privateKey.exists()) {
            privateKey = new File(System.getProperty("user.home"), ".ssh/id_rsa");
        }

        ScpWagon wagon = createWagon();

        wagon.setUIKeyboardInteractive(new DefaultUIKeyboardInteractive(password));
        try {

            Repository repository = new Repository("1", "scp://" + host + ":" + port);

            AuthenticationInfo authenticationInfo = new AuthenticationInfo();
            authenticationInfo.setUserName(username);
            String trimPassword = StringUtils.isEmpty(password) ? null : password;
            authenticationInfo.setPassword(trimPassword);
            authenticationInfo.setPrivateKey(privateKey.getPath());

            wagon.connect(repository, authenticationInfo);

            return new SshShellImpl(wagon, this);

        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

    private ScpWagon createWagon() {

        ScpWagon wagon = new ScpWagon();
        wagon.setInteractive(true);

        if (enableSessionLog) {
            wagon.addSessionListener(new Debug());
        }

        // dont want to handle known host file
        KnownHostsProvider knownHostsProvider = new NullKnownHostProvider();
        knownHostsProvider.setHostKeyChecking("no");

        wagon.setKnownHostsProvider(knownHostsProvider);

        // must put the default, since its default is from plexus, and we invoke it directly
        wagon.setPreferredAuthentications("publickey,password,keyboard-interactive,gssapi-with-mic");
        return wagon;

    }

}
