/*
 * $Id$
 */
package lia.gsi.ssh;

import com.sshtools.common.configuration.SshToolsConnectionProfile;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.session.SessionChannelClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Adrian Muraru
 */
public class TextSSHClient {

    public static void main(String[] args) throws Exception {
        GSIAuthenticationClient gsiAuth = null;
        try {
            gsiAuth = new GSIAuthenticationClient();
            gsiAuth.setUsername(args[1]);
        } catch (Exception e) {
            System.err.println("Cannot load grid credentials.");
            e.printStackTrace();
            return;
        }
        System.out.println("Local GSI Credential loaded.");

        SshClient ssh = new SshClient();
        SshToolsConnectionProfile properties = new SshToolsConnectionProfile();
        properties.setPort(1975);
        properties.setForwardingAutoStartMode(false);
        properties.setHost(args[0]);
        properties.setUsername(args[1]);
        ssh.setUseDefaultForwarding(false);
        ssh.connect(properties);
        System.out.println("Available methods:" + ssh.getAvailableAuthMethods(args[1]));
        try {
            // Authenticate the user
            int result = ssh.authenticate(gsiAuth, args[0]);
            if (result != AuthenticationProtocolState.COMPLETE) {
                // Authentication complete
                System.out.println("Auth failed:" + result);
                return;
            }
            // Open a session channel
            SessionChannelClient session = ssh.openSessionChannel();
            session.requestPseudoTerminal("ansi", 0, 0, 0, 0, "");
            if (!session.executeCommand(args[2])) {
                System.out.println("Command failed");
                ssh.disconnect();
                return;
            }
            BufferedReader bfr = new BufferedReader(new InputStreamReader(session.getInputStream()));
            String line;
            while ((line = bfr.readLine()) != null)
                System.out.println(line);
        } catch (Exception e) {
            e.printStackTrace();
            ssh.disconnect();
        }
    }

}
