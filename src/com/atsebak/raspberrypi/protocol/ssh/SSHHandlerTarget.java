package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.commandline.LinuxCommand;
import com.atsebak.raspberrypi.console.PIConsoleView;
import com.atsebak.raspberrypi.localization.PIBundle;
import com.atsebak.raspberrypi.runner.data.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import lombok.Builder;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Builder
public class SSHHandlerTarget {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String OUTPUT_LOCATION = "IdeaProjects";
    private RaspberryPIRunnerParameters piRunnerParameters;
    private PIConsoleView consoleView;
    private SSHBuilder sshBuilder;

    /** Uploads Java application output folders
     * @param compileOutput Output directory folder where to store the java application
     * @param cmd         The command to execute on the java files
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws RuntimeConfigurationException
     */
    public void uploadAndRunJavaApp(@NotNull final File compileOutput, @NotNull final String cmd)
            throws IOException, ClassNotFoundException, RuntimeConfigurationException {
        final String remoteDir = File.separator + "home" + File.separator + piRunnerParameters.getUsername() + File.separator + OUTPUT_LOCATION;
        String deploymentPath = remoteDir + File.separator + consoleView.getProject().getName();
        genericUpload(deploymentPath, compileOutput);
        consoleView.print(PIBundle.getString("pi.deployment.finished") + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
        runJavaApp(deploymentPath, cmd);
    }

    /**
     * Force create directories, if it exists it won't do anything
     *
     * @param path
     * @throws IOException
     * @throws RuntimeConfigurationException
     */
    private void forceCreateDirectories(String path) throws IOException, RuntimeConfigurationException {
        SSHClient ssh = sshBuilder.toClient();
        connect(ssh);
        final Session session = ssh.startSession();
        String cmd = LinuxCommand.builder()
                .commands(Arrays.asList(
                        String.format("mkdir -p %s", path),
                        String.format("cd %s", path),
                        "rm -rf *"
                )).build().toString();

        consoleView.print(PIBundle.getString("pi.deployment.command") + cmd + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
        session.exec(cmd);
    }

    /**
     * Generic SSh Ftp uploader
     *
     * @param deploymentPath     the remote location storing the compressed file
     * @param fileToUpload files to upload
     * @throws IOException
     * @throws RuntimeConfigurationException
     */
    public void genericUpload(@NotNull final String deploymentPath, @NotNull final File fileToUpload) throws IOException, RuntimeConfigurationException {
        forceCreateDirectories(deploymentPath);
        SSHClient ssh = sshBuilder.toClient();
        connect(ssh);
        try {
            final SFTPClient sftp = ssh.newSFTPClient();
            sftp.getFileTransfer().setTransferListener(new SFTPListener(deploymentPath, consoleView));
            sftp.put(new FileSystemFile(fileToUpload), deploymentPath);
        } finally {
            ssh.disconnect();
        }
    }

    /**
     *  Runs that java app with the specified command and then takes the console output from target to host machine
     * @param path
     * @param cmd
     * @throws IOException
     */
    private void runJavaApp(String path, String cmd) throws IOException, RuntimeConfigurationException {
        consoleView.print(NEW_LINE + PIBundle.getString("pi.deployment.build") + NEW_LINE + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
        final SSHClient sshClient = sshBuilder.toClient();
        connect(sshClient);
        final Session session = sshClient.startSession();


        String jarCmd = LinuxCommand.builder()
                .commands(Arrays.asList(
                        "sudo killall java",
                        String.format("cd %s", path),
                        String.format("tar -xvf %s.tar", consoleView.getProject().getName()),
                        "rm *.tar",
                        cmd
                )).build().toString();
        session.setAutoExpand(true);
        try {
            consoleView.print(PIBundle.getString("pi.deployment.command") + jarCmd + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
            Session.Command exec = session.exec(jarCmd);
            new StreamCopier(exec.getInputStream(), System.out).spawn("stdout");
            new StreamCopier(exec.getErrorStream(), System.err).spawn("stderr");
        } finally {
        }
    }

    private void connect(SSHClient client) throws IOException, RuntimeConfigurationException {
        if (!client.isAuthenticated()) {
            client.connect(piRunnerParameters.getHostname());
            client.authPassword(piRunnerParameters.getUsername(), piRunnerParameters.getPassword());
        }
        if (!client.isAuthenticated() && !client.isConnected()) {
            final Notification notification = new Notification(
                    com.atsebak.raspberrypi.utils.Notifications.GROUPDISPLAY_ID,
                    PIBundle.getString("pi.ssh.connection.error"), PIBundle.getString("pi.ssh.remote.error"),
                    NotificationType.ERROR);
            Notifications.Bus.notify(notification);
            throw new RuntimeConfigurationException(PIBundle.getString("pi.ssh.remote.error"));
        }
    }
}
