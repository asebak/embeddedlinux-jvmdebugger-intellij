package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.localization.PIBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import lombok.Builder;
import net.schmizz.sshj.SSHClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;

@Builder
public class SSHConnectionValidator {
    String ip;
    String username;
    String password;

    public boolean canConnectToHostname() {
        try {
            return InetAddress.getByName(ip).isReachable(200);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * See if user can connect to remote target
     *
     * @param client
     * @param project
     * @throws IOException
     */
    public void checkSSHConnection(SSHClient client, @NotNull Project project) throws IOException {
        try {
            client.connect(ip);
            client.authPassword(username, password);
            Messages.showInfoMessage(project, PIBundle.getString("ssh.connection.success"), PIBundle.getString("pi.connection.success.title"));
        } catch (Exception e) {
            Messages.showErrorDialog(project, PIBundle.getString("pi.ssh.remote.error"),
                    PIBundle.getString("pi.ssh.connection.error"));
        }
    }

}
