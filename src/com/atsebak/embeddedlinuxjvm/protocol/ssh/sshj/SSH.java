package com.atsebak.embeddedlinuxjvm.protocol.ssh.sshj;

import lombok.Builder;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;

@Builder
public class SSH {
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
            sshClient.setConnectTimeout(connectionTimeout);
            sshClient.setTimeout(timeout);
            sshClient.loadKnownHosts();
        } catch (IOException e) {

        }
        return sshClient;
    }
}
