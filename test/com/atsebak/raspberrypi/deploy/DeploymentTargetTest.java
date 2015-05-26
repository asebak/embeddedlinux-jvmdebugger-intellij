package com.atsebak.raspberrypi.deploy;

import com.atsebak.embeddedlinuxjvm.deploy.DeploymentTarget;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSHHandlerTarget;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;

public class DeploymentTargetTest {

    SSHHandlerTarget sshHandlerTarget = Mockito.mock(SSHHandlerTarget.class);
    DeploymentTarget deploymentTarget = DeploymentTarget.builder()
            .sshHandlerTarget(sshHandlerTarget)
            .build();

    @Test(expected = IllegalArgumentException.class)
    public void testNullFileUpload() throws Exception {
        deploymentTarget.upload(null, "");
        Mockito.verify(sshHandlerTarget, never()).genericUpload(anyString(), any(File.class));
    }

    @Test
    public void testGeneric() throws Exception {
        deploymentTarget.upload("/home", new File("/windows"));
        Mockito.verify(sshHandlerTarget).genericUpload("/home", new File("/windows"));
    }
}