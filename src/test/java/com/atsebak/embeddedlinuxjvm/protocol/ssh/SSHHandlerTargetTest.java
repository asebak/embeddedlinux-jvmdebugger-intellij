package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.EmbeddedSSHClient;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.SFTPHandler;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
public class SSHHandlerTargetTest {

    EmbeddedSSHClient sshClient = mock(EmbeddedSSHClient.class);
    EmbeddedLinuxJVMRunConfigurationRunnerParameters piRunnerParameters = mock(EmbeddedLinuxJVMRunConfigurationRunnerParameters.class);
    EmbeddedLinuxJVMConsoleView consoleView = mock(EmbeddedLinuxJVMConsoleView.class);
    final SSHHandlerTarget target = SSHHandlerTarget.builder()
            .ssh(sshClient)
            .consoleView(consoleView)
            .params(piRunnerParameters)
            .build();
    Project project = mock(Project.class);
    File sampleFile = mock(File.class);
    Session session = mock(Session.class);
    ChannelExec channelExec = mock(ChannelExec.class);

    @Before
    public void setup() throws IOException, JSchException {
        when(sshClient.get()).thenReturn(session);
        when(session.openChannel("exec")).thenReturn(channelExec);
        when(session.openChannel("sftp")).thenReturn(channelExec);
        when(consoleView.getProject()).thenReturn(project);
        when(session.isConnected()).thenReturn(true);
        doNothing().when(channelExec).connect();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullUploadPath() throws IOException, RuntimeConfigurationException {
        target.genericUpload(null, sampleFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFile() throws IOException, RuntimeConfigurationException {
        target.genericUpload("/home", null);
    }

    @Test
    @Ignore("Mockito Spying on object not working for now")
    public void verifyUploadToTarget() throws Exception {
        when(piRunnerParameters.getUsername()).thenReturn("ahmad");
        when(project.getName()).thenReturn("untitled");
        SFTPHandler sftpHandler = spy(new SFTPHandler(any(EmbeddedLinuxJVMConsoleView.class)));
        doNothing().when(sftpHandler).upload(any(Session.class), any(File.class), anyString());
        target.uploadAndRunJavaApp(sampleFile, "java -jar");

        verify(channelExec, times(2)).setCommand(anyString());
        verify(channelExec, times(2)).connect();

    }

}