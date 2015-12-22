package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.EmbeddedSSHClient;
import com.jcraft.jsch.Session;
import lombok.Builder;

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
     *
     * @return status
     */
    public boolean checkSSHConnection() {
        try {
            EmbeddedSSHClient sshClient = EmbeddedSSHClient.builder()
                    .username(username).password(password).hostname(ip).build();
            Session session = sshClient.get();
            return session.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

}
