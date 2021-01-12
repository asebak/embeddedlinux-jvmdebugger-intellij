package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.EmbeddedSSHClient;
import com.jcraft.jsch.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
    private int port;

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
    public SSHConnectionState checkSSHConnection() {
        try {
            EmbeddedSSHClient sshClient = EmbeddedSSHClient
                    .builder()
                    .port(port)
                    .username(username)
                    .password(password)
                    .hostname(ip)
                    .key(key)
                    .useKey(useKey)
                    .build();
            Session session = sshClient.get();
            return new SSHConnectionValidator.SSHConnectionState(session.isConnected(), null);
        } catch (Exception e) {
            return new SSHConnectionValidator.SSHConnectionState(false, e.getMessage());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SSHConnectionState {
        private boolean connected;
        private String message;

    }

}
