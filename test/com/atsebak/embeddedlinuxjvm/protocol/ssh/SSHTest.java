package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.atsebak.embeddedlinuxjvm.protocol.ssh.sshj.SSH;
import net.schmizz.sshj.SSHClient;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SSHTest {

    @Test
    public void testToClient() throws Exception {
        SSHClient sshClient = SSH.builder()
                .connectionTimeout(100)
                .timeout(200)
                .build()
                .toClient();

        assertEquals(sshClient.getTimeout(), 200);
        assertEquals(sshClient.getConnectTimeout(), 100);

    }


}