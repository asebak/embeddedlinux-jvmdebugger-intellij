package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.console.PIConsoleView;
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

@Builder
public class SSHHandlerTarget {
    private static final String NEW_LINE = System.getProperty("line.separator");
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
        final String remoteDirec = File.separator + "home" + File.separator + piRunnerParameters.getUsername() + File.separator + "IdeaProjects";
        genericUpload(remoteDirec, compileOutput);
        consoleView.print("Finished Deploying App" + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
        String appPath = remoteDirec + File.separator + compileOutput.getName();
        runJavaApp(appPath, cmd);
    }

    /**
     * Generic SSh Ftp uploader
     *
     * @param uploadTo     the remote location
     * @param fileToUpload files to upload
     * @throws IOException
     * @throws RuntimeConfigurationException
     */
    public void genericUpload(@NotNull final String uploadTo, @NotNull final File fileToUpload) throws IOException, RuntimeConfigurationException {
        SSHClient ssh = sshBuilder.toClient();
        connect(ssh);
        try {
            final SFTPClient sftp = ssh.newSFTPClient();
            sftp.put(new FileSystemFile(fileToUpload), uploadTo);
        } finally {
            ssh.disconnect();
        }
    }

    /**
     *  Runs that java app with the specified command and then takes the console output from target to host machine
     * @param targetPathOnRemote
     * @param cmd
     * @throws IOException
     */
    private void runJavaApp(String targetPathOnRemote, String cmd) throws IOException, RuntimeConfigurationException {
        consoleView.print(NEW_LINE + ">>>>>>>>> You're Building on Embedded Linux <<<<<<<<" + NEW_LINE + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
        final SSHClient sshClient = sshBuilder.toClient();
        connect(sshClient);
        final Session session = sshClient.startSession();
        final String cmdExecute = "sudo killall java; cd " + targetPathOnRemote + "; " + cmd;
        session.setAutoExpand(true);
        try {
            consoleView.print("Executing Command: " + cmdExecute + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
            Session.Command exec = session.exec(cmdExecute);
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
                    com.atsebak.raspberrypi.utils.Notifications.GROUPDISPLAY_ID, "SSH Connection Error", "Could not connect to remote target",
                    NotificationType.ERROR);
            Notifications.Bus.notify(notification);
            throw new RuntimeConfigurationException("Cannot Authenticate With Remote Device");
        }
    }
}
