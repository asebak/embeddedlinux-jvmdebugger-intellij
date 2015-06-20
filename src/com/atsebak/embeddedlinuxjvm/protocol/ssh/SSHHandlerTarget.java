package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.atsebak.embeddedlinuxjvm.commandline.LinuxCommand;
import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.deploy.DeployedLibrary;
import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.atsebak.embeddedlinuxjvm.utils.FileUtilities;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import lombok.Builder;
import lombok.SneakyThrows;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
public class SSHHandlerTarget {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String OUTPUT_LOCATION = "IdeaProjects";
    private EmbeddedLinuxJVMRunConfigurationRunnerParameters piRunnerParameters;
    private EmbeddedLinuxJVMConsoleView consoleView;
    private SSH ssh;

    public List<DeployedLibrary> listAlreadyUploadedJars() throws IOException, RuntimeConfigurationException {
        final String remoteDir = FileUtilities.SEPARATOR + "home" + FileUtilities.SEPARATOR
                + piRunnerParameters.getUsername() + FileUtilities.SEPARATOR + OUTPUT_LOCATION;
        String path = remoteDir + FileUtilities.SEPARATOR + consoleView.getProject().getName();
        String deploymentPathJars = path + FileUtilities.SEPARATOR + FileUtilities.LIB;
        SSHClient ssh = this.ssh.toClient();
        connect(ssh);
        Session session = ssh.startSession();
        String cmd = LinuxCommand.builder().commands(
                Arrays.asList(
                        String.format("cd %s", deploymentPathJars),
                        "du -h --max-depth=0 * --time"
                ))
                .build()
                .toString();
        List<DeployedLibrary> libraries = new ArrayList<DeployedLibrary>();
        try {
            Session.Command exec = session.exec(cmd);
            String files = IOUtils.readFully(exec.getInputStream()).toString();
            if (StringUtils.isNotBlank(files)) {
                String[] unparsedFiles = files.split("\\r?\\n");
                for (int i = 0; i < unparsedFiles.length; i++) {
                    String[] splitDescription = unparsedFiles[i].split("\t");
                    libraries.add(DeployedLibrary.builder()
                            .size(splitDescription[0])
                            .lastModified(splitDescription[1])
                            .jarName(splitDescription[2])
                            .build());
                }
            }
        } finally {
            session.close();
            ssh.disconnect();
        }

        return libraries;
    }

    /** Uploads Java application output folders
     * @param compileOutput Output directory folder where to store the java application
     * @param cmd         The command to execute on the java files
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws RuntimeConfigurationException
     */
    public void uploadAndRunJavaApp(@NotNull final File compileOutput, @NotNull final String cmd)
            throws IOException, ClassNotFoundException, RuntimeConfigurationException {
        final String remoteDir = FileUtilities.SEPARATOR + "home" + FileUtilities.SEPARATOR
                + piRunnerParameters.getUsername() + FileUtilities.SEPARATOR + OUTPUT_LOCATION;
        String deploymentPath = remoteDir + FileUtilities.SEPARATOR + consoleView.getProject().getName();
        genericUpload(deploymentPath, compileOutput);
        consoleView.print(EmbeddedLinuxJVMBundle.getString("pi.deployment.finished") + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
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
        SSHClient ssh = this.ssh.toClient();
        connect(ssh);
        final Session session = ssh.startSession();
        String cmd = LinuxCommand.builder().commands(
                Arrays.asList(
                        String.format("mkdir -p %s", path),
                        String.format("cd %s", path),
                        String.format("mkdir -p %s", FileUtilities.CLASSES),
                        String.format("mkdir -p %s", FileUtilities.LIB),
                        String.format("cd %s", path + FileUtilities.SEPARATOR + FileUtilities.CLASSES),
                        "rm -rf *"
                ))
                .build()
                .toString();

        consoleView.print(EmbeddedLinuxJVMBundle.getString("pi.deployment.command") + cmd + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
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
        SSHClient ssh = this.ssh.toClient();
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
        consoleView.print(NEW_LINE + EmbeddedLinuxJVMBundle.getString("pi.deployment.build") + NEW_LINE + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
        final SSHClient sshClient = ssh.toClient();
        connect(sshClient);
        final Session session = sshClient.startSession();

        String jarCmd = LinuxCommand.builder().commands
                (Arrays.asList(
                        String.format("sudo kill -9 $(ps -efww | grep \"%s\"| grep -v grep | tr -s \" \"| cut -d\" \" -f2)", piRunnerParameters.getMainclass()),
                        String.format("cd %s", path),
                        String.format("tar -xvf %s.tar", consoleView.getProject().getName()),
                        "rm *.tar",
                        cmd))
                .build()
                .toString();
        consoleView.print(EmbeddedLinuxJVMBundle.getString("pi.deployment.command") + jarCmd + NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
        Session.Command exec = session.exec(jarCmd);
        consoleView.setCommand(exec);
        new StreamCopier(exec.getInputStream(), System.out).spawn("stdout");
        new StreamCopier(exec.getErrorStream(), System.err).spawn("stderr");
    }

    /**
     * Authenticates and connects to remote target via ssh protocol
     *
     * @param client
     * @throws IOException
     * @throws RuntimeConfigurationException
     */
    @SneakyThrows({IOException.class, RuntimeConfigurationException.class})
    private void connect(SSHClient client) {
        if (!client.isAuthenticated()) {
            client.connect(piRunnerParameters.getHostname());
            client.authPassword(piRunnerParameters.getUsername(), piRunnerParameters.getPassword());
        }
        if (!client.isAuthenticated() && !client.isConnected()) {
            final Notification notification = new Notification(
                    com.atsebak.embeddedlinuxjvm.utils.Notifications.GROUPDISPLAY_ID,
                    EmbeddedLinuxJVMBundle.getString("pi.ssh.connection.error"), EmbeddedLinuxJVMBundle.getString("ssh.remote.error"),
                    NotificationType.ERROR);
            Notifications.Bus.notify(notification);
            throw new RuntimeConfigurationException(EmbeddedLinuxJVMBundle.getString("ssh.remote.error"));
        }
    }
}
