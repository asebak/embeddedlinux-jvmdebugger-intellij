package com.atsebak.raspberrypi.protocol.ssh;

import com.intellij.execution.configurations.RuntimeConfigurationException;
import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by asebak on 28/03/15.
 */
public class SSHHandler {
    private final String hostname;
    private final String username;
    private final String password;

    public SSHHandler(String hostname, String username, String password) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    public void startConnection() throws IOException, InterruptedException {
        DefaultConfig defaultConfig = new DefaultConfig();
        defaultConfig.setKeepAliveProvider(KeepAliveProvider.KEEP_ALIVE);
        final SSHClient ssh = new SSHClient(defaultConfig);
        try {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(hostname);
            ssh.getConnection().getKeepAlive().setKeepAliveInterval(5); //every 60sec
            ssh.authPassword(username, password);
            Session session = ssh.startSession();
            session.allocateDefaultPTY();
            new CountDownLatch(1).await();
            try {
                session.allocateDefaultPTY();
            } finally {
                session.close();
            }
        } finally {
            ssh.disconnect();
        }
    }

    public void upload(File outputDirec) throws IOException, ClassNotFoundException, RuntimeConfigurationException {
        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.loadKnownHosts();
        ssh.connect(hostname);
        try {
            ssh.authPassword(username, password);
        } catch (UserAuthException e) {
            throw new RuntimeConfigurationException("Authentication Failed For Raspberry PI");
        }
        // ssh.authPassword(username, password);
        try {
//            ssh.authPublickey(System.getProperty("user.name"));
            //ssh.useCompression();
            final String src = System.getProperty("user.home") + File.separator + "intellij_plugin_test";
            final SFTPClient sftp = ssh.newSFTPClient();
            sftp.put(new FileSystemFile(src), "/tmp");
        } finally {
            ssh.disconnect();
        }
    }


    public void executeCommand(String command) throws IOException {
        final SSHClient ssh = new SSHClient();
        ssh.loadKnownHosts();
        ssh.connect(hostname);
        try {
            ssh.authPublickey(System.getProperty("user.name"));
            final Session session = ssh.startSession();
            try {
                final Session.Command cmd = session.exec(command);
                System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());
                cmd.join(5, TimeUnit.SECONDS);
                System.out.println("\n** exit status: " + cmd.getExitStatus());
            } finally {
                session.close();
            }
        } finally {
            ssh.disconnect();
        }
    }
}
