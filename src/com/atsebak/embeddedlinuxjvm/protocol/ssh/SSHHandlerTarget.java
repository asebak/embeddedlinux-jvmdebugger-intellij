package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.atsebak.embeddedlinuxjvm.commandline.JavaStatusChecker;
import com.atsebak.embeddedlinuxjvm.commandline.LinuxCommand;
import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.EmbeddedSSHClient;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.SFTPHandler;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.atsebak.embeddedlinuxjvm.utils.FileUtilities;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Builder;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Builder
public class SSHHandlerTarget {
    public static final String NEW_LINE = System.getProperty("line.separator");
    private static final String OUTPUT_LOCATION = "IdeaProjects";
    private static final String EMBEDDED_LINUX_HOME = "home";
    private EmbeddedLinuxJVMRunConfigurationRunnerParameters params;
    private EmbeddedLinuxJVMConsoleView consoleView;
    private EmbeddedSSHClient ssh;

    /**
     * Uploads Java application output folders
     *
     * @param compileOutput Output directory folder where to store the java application
     * @param cmd           The command to execute on the java files
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws RuntimeConfigurationException
     */
    public void uploadAndRunJavaApp(@NotNull final File compileOutput, @NotNull final String cmd)
            throws IOException, ClassNotFoundException, RuntimeConfigurationException {
        final String remoteDir = FileUtilities.SEPARATOR + EMBEDDED_LINUX_HOME + FileUtilities.SEPARATOR
                + params.getUsername() + FileUtilities.SEPARATOR + OUTPUT_LOCATION;
        String deploymentPath = remoteDir + FileUtilities.SEPARATOR + consoleView.getProject().getName();
        genericUpload(deploymentPath, compileOutput);
        runJavaApp(deploymentPath, cmd);
    }

    /**
     * Generic SSh Ftp uploader
     *
     * @param deploymentPath the remote location storing the compressed file
     * @param fileToUpload   files to upload
     * @throws IOException
     * @throws RuntimeConfigurationException
     */
    public void genericUpload(@NotNull final String deploymentPath, @NotNull final File fileToUpload) throws IOException, RuntimeConfigurationException {
        forceCreateDirectories(deploymentPath);
        Session session = connect(ssh.get());
        try {
            SFTPHandler sftpHandler = new SFTPHandler(consoleView);
            sftpHandler.upload(session, fileToUpload, deploymentPath);
        } catch (Exception e) {
            setErrorOnUI(e.getMessage());
        }
    }


    /**
     * Force create directories, if it exists it won't do anything
     *
     * @param path
     * @throws IOException
     * @throws RuntimeConfigurationException
     */
    private void forceCreateDirectories(@NotNull final String path) throws IOException, RuntimeConfigurationException {
        Session session = connect(ssh.get());
        try {
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            List<String> commands = Arrays.asList(
                    String.format("mkdir -p %s", path),
                    String.format("cd %s", path),
                    String.format("mkdir -p %s", FileUtilities.CLASSES),
                    String.format("mkdir -p %s", FileUtilities.LIB),
                    String.format("cd %s", path + FileUtilities.SEPARATOR + FileUtilities.CLASSES),
                    "rm -rf *"
            );
            for (String command : commands) {
                consoleView.print(EmbeddedLinuxJVMBundle.getString("pi.deployment.command") + command + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
            }
            channelExec.setCommand(LinuxCommand.builder().commands(commands).build().toString());
            channelExec.connect();
            channelExec.disconnect();
        } catch (JSchException e) {
            setErrorOnUI(e.getMessage());
        }
    }

    /**
     * Runs that java app with the specified command and then takes the console output from target to host machine
     *
     * @param path
     * @param cmd
     * @throws IOException
     */
    private void runJavaApp(@NotNull final String path, @NotNull final String cmd) throws IOException, RuntimeConfigurationException {
        consoleView.print(NEW_LINE + EmbeddedLinuxJVMBundle.getString("pi.deployment.build") + NEW_LINE + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
        Session session = connect(ssh.get());
        try {
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setOutputStream(System.out, true);
            channelExec.setErrStream(System.err, true);
            List<String> commands = Arrays.asList(
                    String.format("%s kill -9 $(ps -efww | grep \"%s\"| grep -v grep | tr -s \" \"| cut -d\" \" -f2)", params.isRunAsRoot() ? "sudo" : "", params.getMainclass()),
                    String.format("cd %s", path),
                    String.format("tar -xvf %s.tar", consoleView.getProject().getName()),
                    "rm *.tar",
                    cmd);
            for (String command : commands) {
                consoleView.print(EmbeddedLinuxJVMBundle.getString("pi.deployment.command") + command + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
            }
            channelExec.setCommand(LinuxCommand.builder().commands(commands).build().toString());
            channelExec.connect();
            checkOnProcess(channelExec);
        } catch (JSchException e) {
            setErrorOnUI(e.getMessage());
        }
    }

    /**
     * Checks if command is done running
     */
    private void checkOnProcess(@NotNull final ChannelExec channelExec) {
        ApplicationManager.getApplication().executeOnPooledThread(new JavaStatusChecker(channelExec, consoleView));
    }

    /**
     * Authenticates and connects to remote target via ssh protocol
     * @param session
     * @return
     */
    @SneakyThrows({RuntimeConfigurationException.class})
    private Session connect(Session session) {
        if (!session.isConnected()) {
            session = EmbeddedSSHClient.builder().username(params.getUsername())
                    .password(params.getPassword()).hostname(params.getHostname()).build().get();
            if (!session.isConnected()) {
                setErrorOnUI(EmbeddedLinuxJVMBundle.getString("ssh.remote.error"));
                throw new RuntimeConfigurationException(EmbeddedLinuxJVMBundle.getString("ssh.remote.error"));
            } else {
                return session;
            }
        }
        return session;
    }

    /**
     * Sets errors on the UI
     *
     * @param message
     */
    private void setErrorOnUI(@NotNull final String message) {
        final Notification notification = new Notification(
                com.atsebak.embeddedlinuxjvm.utils.Notifications.GROUPDISPLAY_ID,
                EmbeddedLinuxJVMBundle.getString("pi.ssh.connection.error"), message,
                NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
    }
}
