package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.jcraft.jsch.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;

public class SFTPHandler {
    private EmbeddedLinuxJVMConsoleView consoleView;

    public SFTPHandler(@NotNull EmbeddedLinuxJVMConsoleView consoleView) {
        this.consoleView = consoleView;
    }

    /**
     * Uploads to target
     *
     * @param session
     * @param upload
     * @param deploymentPath
     * @throws JSchException
     * @throws SftpException
     * @throws FileNotFoundException
     */
    public void upload(@NotNull Session session, @NotNull final File upload, String deploymentPath) throws JSchException, SftpException, FileNotFoundException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        final ChannelSftp channelSftp = (ChannelSftp) channel;
        channelSftp.cd(deploymentPath);
        channelSftp.put(upload.getAbsolutePath(), upload.getName(), new SFTPProgress(consoleView), ChannelSftp.OVERWRITE);
    }

}
