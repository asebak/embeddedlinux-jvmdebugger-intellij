package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.intellij.openapi.project.Project;
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

    /**
     * Pings to see if it can contact hostname
     *
     * @return
     */
    public boolean canConnectToHostname(int timeout) {
        try {
            return InetAddress.getByName(ip).isReachable(timeout);
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Can connect to remote target
     * @param client
     * @param project
     * @return status
     */
    public boolean checkSSHConnection(SSHClient client, @NotNull Project project) {
        try {
            client.connect(ip);
            client.authPassword(username, password);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
