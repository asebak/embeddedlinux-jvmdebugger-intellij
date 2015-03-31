package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.console.PIConsoleView;
import com.atsebak.raspberrypi.runner.data.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import lombok.Builder;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.File;
import java.io.IOException;

@Builder
public class SSHHandler {
    private Project project;
    private RaspberryPIRunnerParameters piRunnerParameters;


    /** Uploads Java application output folders
     * @param outputDirec Output directory folder where to store the java application
     * @param cmd         The command to execute on the java files
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws RuntimeConfigurationException
     */
    public void upload(final File outputDirec, final String cmd)
            throws IOException, ClassNotFoundException, RuntimeConfigurationException {
        SSHClient ssh = build(new SSHClient());
        final String remoteDirec = File.separator + "home" + File.separator + piRunnerParameters.getUsername() + File.separator + "IdeaProjects";
        try {
            final SFTPClient sftp = ssh.newSFTPClient();
            sftp.put(new FileSystemFile(outputDirec), remoteDirec);
            PIConsoleView.getInstance(project).print("Finished Deploying App\n\r", ConsoleViewContentType.SYSTEM_OUTPUT);
        } finally {
            ssh.disconnect();
        }
        String appPath = remoteDirec + File.separator + outputDirec.getName();
        runJavaApp(appPath, cmd);
    }

    /**
     *
     * @param client The SSHClient
     * @return Builds an SSh Client
     * @throws IOException
     */
    private SSHClient build(SSHClient client) throws IOException, RuntimeConfigurationException {
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.loadKnownHosts();
        client.setConnectTimeout(3000);
        if (!client.isAuthenticated()) {
            client.connect(piRunnerParameters.getHostname());
            client.authPassword(piRunnerParameters.getUsername(), piRunnerParameters.getPassword());
        }
        if (!client.isAuthenticated() || !client.isConnected()) {
            final Notification notification = new Notification(
                    com.atsebak.raspberrypi.utils.Notifications.GROUPDISPLAY_ID, "SSH Connection Error", "Could not connect to remote target",
                    NotificationType.ERROR);
            Notifications.Bus.notify(notification);
            throw new RuntimeConfigurationException("Cannot Authenticate With Remote Device");
        }
        return client;
    }

    /**
     *  Runs that java app with the specified command and then takes the console output from target to host machine
     * @param targetPathOnRemote
     * @param cmd
     * @throws IOException
     */
    private void runJavaApp(String targetPathOnRemote, String cmd) throws IOException, RuntimeConfigurationException {
        PIConsoleView.getInstance(project).print(">>>>>>>>> You're Building on Embedded Linux <<<<<<<<\n\r", ConsoleViewContentType.SYSTEM_OUTPUT);

        final SSHClient sshClient = build(new SSHClient());
        final Session session = sshClient.startSession();
        //kill existing process, change to java folder and run it.
        //todo kill only the java process using that tcp port, how to do that in one line?
        final String cmdExecute = "sudo killall java; cd " + targetPathOnRemote + "; " + cmd;
        session.setAutoExpand(true);
        try {
            PIConsoleView.getInstance(project).print("Executing Command: " + cmdExecute + " \n\r", ConsoleViewContentType.SYSTEM_OUTPUT);
            Session.Command exec = session.exec(cmdExecute);
            new StreamCopier(exec.getInputStream(), System.out).spawn("stdout");
            new StreamCopier(exec.getErrorStream(), System.err).spawn("stderr");
        } finally {
            //todo is this needed?
//            session.close();
//            sshClient.close();
        }
    }
}
