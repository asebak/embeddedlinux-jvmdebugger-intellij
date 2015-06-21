package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.atsebak.embeddedlinuxjvm.utils.FileUtilities;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPFileTransfer;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.TransferListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
public class SSHHandlerTargetTest {

    SSH ssh = Mockito.mock(SSH.class);
    SSHClient sshClient = Mockito.mock(SSHClient.class);
    EmbeddedLinuxJVMRunConfigurationRunnerParameters piRunnerParameters = Mockito.mock(EmbeddedLinuxJVMRunConfigurationRunnerParameters.class);
    EmbeddedLinuxJVMConsoleView consoleView = Mockito.mock(EmbeddedLinuxJVMConsoleView.class);
    final SSHHandlerTarget target = SSHHandlerTarget.builder()
            .ssh(ssh)
            .consoleView(consoleView)
            .piRunnerParameters(piRunnerParameters)
            .build();
    Project project = Mockito.mock(Project.class);
    File sampleFile = Mockito.mock(File.class);
    SFTPClient sftpClient = Mockito.mock(SFTPClient.class);
    Session session = Mockito.mock(Session.class);
    Session.Command command = Mockito.mock(Session.Command.class);
    SFTPFileTransfer sftpFileTransfer = Mockito.mock(SFTPFileTransfer.class);
    @Before
    public void setup() throws IOException {
        Mockito.when(ssh.toClient()).thenReturn(sshClient);
        Mockito.when(consoleView.getProject()).thenReturn(project);
        Mockito.when(sshClient.newSFTPClient()).thenReturn(sftpClient);
        Mockito.when(sshClient.startSession()).thenReturn(session);
        Mockito.when(session.exec(anyString())).thenReturn(command);
        Mockito.when(sftpClient.getFileTransfer()).thenReturn(sftpFileTransfer);
        Mockito.doNothing().when(sftpFileTransfer).setTransferListener(Matchers.<TransferListener>anyObject());
    }

    @Test
    public void testAgenericAuthenticatedUpload() throws Exception {
        Mockito.when(sshClient.isAuthenticated()).thenReturn(true);
        Mockito.when(sshClient.isConnected()).thenReturn(true);

        Mockito.doNothing().when(sftpFileTransfer).setTransferListener(Matchers.<TransferListener>anyObject());
        target.genericUpload("/home/pi", sampleFile);

        Mockito.verify(sshClient).startSession();
        Mockito.verify(session).exec(anyString());

        Mockito.verify(sftpClient).put(any(FileSystemFile.class), eq("/home/pi"));
        Mockito.verify(sshClient).disconnect();
    }

    @Test(expected = Exception.class)
    public void testGenericNotAuthenticatedUpload() throws IOException, RuntimeConfigurationException {
        PowerMockito.mockStatic(Notifications.Bus.class);
        Mockito.when(sshClient.isAuthenticated()).thenReturn(false);
        Mockito.when(sshClient.isConnected()).thenReturn(false);

        target.genericUpload("/home/pi", sampleFile);
    }
    @Test(expected = IllegalArgumentException.class)
    public void nullUploadPath() throws IOException, RuntimeConfigurationException {
        Mockito.when(sshClient.isAuthenticated()).thenReturn(true);
        Mockito.when(sshClient.isConnected()).thenReturn(true);
        target.genericUpload(null, sampleFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFile() throws IOException, RuntimeConfigurationException {
        Mockito.when(sshClient.isAuthenticated()).thenReturn(true);
        Mockito.when(sshClient.isConnected()).thenReturn(true);
        target.genericUpload("/home", null);
    }

    @Test
    public void verifyUploadToTarget() throws RuntimeConfigurationException, IOException, ClassNotFoundException {
        Mockito.when(sshClient.isAuthenticated()).thenReturn(true);
        Mockito.when(sshClient.isConnected()).thenReturn(true);

        Mockito.when(piRunnerParameters.getUsername()).thenReturn("ahmad");
        Mockito.when(project.getName()).thenReturn("untitled");
        target.uploadAndRunJavaApp(sampleFile, "java -jar");
        Mockito.verify(sftpClient).put(any(FileSystemFile.class),
                eq(FileUtilities.SEPARATOR + "home" + FileUtilities.SEPARATOR + "ahmad" + FileUtilities.SEPARATOR + "IdeaProjects" + FileUtilities.SEPARATOR + "untitled"));
        Mockito.verify(sshClient).disconnect();
        Mockito.verify(sshClient, times(2)).startSession();

        Mockito.verify(session, times(2)).exec(anyString());
        Mockito.verify(command).getErrorStream();
        Mockito.verify(command).getInputStream();

    }

    @Test
    public void verifyJavaCommands() throws IOException, RuntimeConfigurationException, ClassNotFoundException {
        final String path = FileUtilities.SEPARATOR + "home" + FileUtilities.SEPARATOR + "ahmad" + FileUtilities.SEPARATOR + "IdeaProjects" + FileUtilities.SEPARATOR + "untitled";
        final String commandToBeExecuted = "mkdir -p " + path + "; " + "cd " + path + "; mkdir -p classes; mkdir -p lib; " + "cd " + path + FileUtilities.SEPARATOR + FileUtilities.CLASSES + "; rm -rf *;";
        Mockito.when(sshClient.isAuthenticated()).thenReturn(true);
        Mockito.when(project.getName()).thenReturn("untitled");
        Mockito.when(sshClient.isConnected()).thenReturn(true);
        Mockito.when(piRunnerParameters.getUsername()).thenReturn("ahmad");
        Mockito.doNothing().when(sftpFileTransfer).setTransferListener(Matchers.<TransferListener>anyObject());

        target.uploadAndRunJavaApp(sampleFile, "java -jar");
        Mockito.verify(session).exec(eq(commandToBeExecuted));
    }


}