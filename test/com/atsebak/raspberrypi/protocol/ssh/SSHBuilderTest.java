package com.atsebak.raspberrypi.protocol.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class SSHBuilderTest {

    @Test
    public void testToClient() throws Exception {
        SSHClient sshClientMock = mock(SSHClient.class);
        when(sshClientMock.isAuthenticated()).thenReturn(true);
        Mockito.doNothing().when(sshClientMock).addHostKeyVerifier(Matchers.<HostKeyVerifier>anyObject());
        verify(sshClientMock, never()).authPassword("ahmad", "sebak");
        SSHClient sshClient = SSHBuilder.builder()
                .sshClient(sshClientMock)
                .connectionTimeout(100)
                .timeout(200)
                .build()
                .toClient();

        assertEquals(sshClient.getTimeout(), 200);
        assertEquals(sshClient.getConnectTimeout(), 100);

    }


}