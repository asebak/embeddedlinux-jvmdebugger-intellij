package com.atsebak.raspberrypi.protocol.ssh;

import lombok.Builder;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;

@Builder
public class SSHBuilder {
    private int timeout;
    private int connectionTimeout;

    /**
     * Build an SSHJ SSH Client
     *
     * @return
     */
    public SSHClient toClient() {
        SSHClient sshClient = new SSHClient();
        try {
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.loadKnownHosts();
            sshClient.setConnectTimeout(connectionTimeout);
            sshClient.setTimeout(timeout);
        } catch (IOException e) {

        }
        return sshClient;
    }
}
