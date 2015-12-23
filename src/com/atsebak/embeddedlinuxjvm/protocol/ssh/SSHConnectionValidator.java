package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.EmbeddedSSHClient;
import com.jcraft.jsch.Session;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;

@Builder
public class SSHConnectionValidator {
    private String ip;
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String key;
    private boolean useKey;

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
            EmbeddedSSHClient sshClient = EmbeddedSSHClient
                    .builder()
                    .username(username).password(password).hostname(ip)
                    .key(key).useKey(useKey).build();
            Session session = sshClient.get();
            return session.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

}
