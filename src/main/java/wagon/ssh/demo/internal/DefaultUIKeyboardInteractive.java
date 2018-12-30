package wagon.ssh.demo.internal;

import com.jcraft.jsch.UIKeyboardInteractive;

public class DefaultUIKeyboardInteractive implements UIKeyboardInteractive {

    private final String []  password = new String[1];

    public DefaultUIKeyboardInteractive(String password)
    {
        this.password[0] = password;
    }


    @Override
    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {

        if ( prompt.length == 0 ) {

            throw new IllegalArgumentException(instruction);
        }
        return password;
    }

}
