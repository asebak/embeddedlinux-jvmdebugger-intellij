package com.atsebak.raspberrypi.protocol.ssh;

import com.intellij.execution.configurations.RuntimeConfigurationException;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.File;
import java.io.IOException;

public class SSHHandler {
    private final String hostname;
    private final String username;
    private final String password;

    /**
     * @param hostname The Address
     * @param username The username
     * @param password The Password
     */
    public SSHHandler(final String hostname, final String username, final String password) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    /** Uploads Java application output folders
     * @param outputDirec Output directory folder where to store the java application
     * @param cmd         The command to execute on the java files
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws RuntimeConfigurationException
     */
    public void upload(final File outputDirec, final String cmd) throws IOException, ClassNotFoundException, RuntimeConfigurationException {
        SSHClient ssh = build(new SSHClient());
        final String remoteDirec = File.separator + "home" + File.separator + username + File.separator + "IdeaProjects";
        try {
            final SFTPClient sftp = ssh.newSFTPClient();
            sftp.put(new FileSystemFile(outputDirec), remoteDirec);
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
    private SSHClient build(SSHClient client) throws IOException {
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.loadKnownHosts();
        client.connect(hostname);
        client.authPassword(username, password);
        return client;
    }

    /**
     *  Runs that java app with the specified command and then takes the console output from target to host machine
     * @param targetPathOnRemote
     * @param cmd
     * @throws IOException
     */
    private void runJavaApp(String targetPathOnRemote, String cmd) throws IOException {
        final SSHClient sshClient = build(new SSHClient());
        final Session session = sshClient.startSession();
        session.setAutoExpand(true);
        try {
            //kill existing process, change to java folder and run it.
            Session.Command exec = session.exec("sudo killall java; cd " + targetPathOnRemote + "; " + cmd);
            new StreamCopier(exec.getInputStream(), System.out).spawn("stdout");
            new StreamCopier(exec.getErrorStream(), System.err).spawn("stderr");
        } finally {
            //todo is this needed?
//            session.close();
//            sshClient.close();
        }
    }
}
