package com.atsebak.raspberrypi.protocol.ssh;

import lombok.Builder;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;

@Builder
public class SSHBuilder {
    private SSHClient sshClient;
    private int timeout;
    private int connectionTimeout;

    public SSHClient toClient() {
        try {
            sshClient = new SSHClient();
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.loadKnownHosts();
            sshClient.setConnectTimeout(connectionTimeout);
            sshClient.setTimeout(timeout);
        } catch (IOException e) {

        }
        return sshClient;
    }
}
