package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.console.PIConsoleView;
import com.atsebak.raspberrypi.runner.data.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.notification.Notifications;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.*;

@RunWith(PowerMockRunner.class)
public class SSHHandlerTargetTest {

    SSHBuilder sshBuilder = Mockito.mock(SSHBuilder.class);
    SSHClient sshClient = Mockito.mock(SSHClient.class);
    RaspberryPIRunnerParameters piRunnerParameters = Mockito.mock(RaspberryPIRunnerParameters.class);
    PIConsoleView consoleView = Mockito.mock(PIConsoleView.class);
    final SSHHandlerTarget target = SSHHandlerTarget.builder()
            .sshBuilder(sshBuilder)
            .consoleView(consoleView)
            .piRunnerParameters(piRunnerParameters)
            .build();
    File sampleFile = Mockito.mock(File.class);
    SFTPClient sftpClient = Mockito.mock(SFTPClient.class);
    Session session = Mockito.mock(Session.class);
    Session.Command command = Mockito.mock(Session.Command.class);

    @Before
    public void setup() {
        Mockito.when(sshBuilder.toClient()).thenReturn(sshClient);
    }

    @Test
    public void testAgenericAuthenticatedUpload() throws Exception {
        Mockito.when(sshClient.isAuthenticated()).thenReturn(true);
        Mockito.when(sshClient.isConnected()).thenReturn(true);
        Mockito.when(sshClient.newSFTPClient()).thenReturn(sftpClient);

        target.genericUpload("/home/pi", sampleFile);

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
        Mockito.when(sshClient.startSession()).thenReturn(session);
        Mockito.when(session.exec(anyString())).thenReturn(command);
        Mockito.when(piRunnerParameters.getUsername()).thenReturn("ahmad");
        Mockito.when(sshClient.newSFTPClient()).thenReturn(sftpClient);
        target.uploadAndRunJavaApp(sampleFile, "java -jar");
        Mockito.verify(sftpClient).put(any(FileSystemFile.class), eq(File.separator + "home" + File.separator + "ahmad" + File.separator + "IdeaProjects"));
        Mockito.verify(sshClient).disconnect();
        Mockito.verify(sshClient).startSession();
        Mockito.verify(session).exec(anyString());
        Mockito.verify(command).getErrorStream();
        Mockito.verify(command).getInputStream();

    }

    @Test
    public void verifyJavaCommands() throws IOException, RuntimeConfigurationException, ClassNotFoundException {
        final String path = File.separator + "home" + File.separator + "ahmad" + File.separator + "IdeaProjects" + File.separator + "SampleProject";
        final String commandToBeExecuted = "sudo killall java; cd " + path + "; java -jar";
        Mockito.when(sshClient.isAuthenticated()).thenReturn(true);
        Mockito.when(sampleFile.getName()).thenReturn("SampleProject");
        Mockito.when(sshClient.isConnected()).thenReturn(true);
        Mockito.when(sshClient.startSession()).thenReturn(session);
        Mockito.when(session.exec(anyString())).thenReturn(command);
        Mockito.when(piRunnerParameters.getUsername()).thenReturn("ahmad");
        Mockito.when(sshClient.newSFTPClient()).thenReturn(sftpClient);
        target.uploadAndRunJavaApp(sampleFile, "java -jar");
        Mockito.verify(session).exec(eq(commandToBeExecuted));
    }


}