package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.jcraft.jsch.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

@AllArgsConstructor
@NoArgsConstructor
public class SFTPHandler {
    private EmbeddedLinuxJVMConsoleView consoleView;


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

    public Vector getFiles(@NotNull Session session, @NotNull final String directory) throws JSchException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        final ChannelSftp channelSftp = (ChannelSftp) channel;
        try {
            channelSftp.cd(directory);
            return channelSftp.ls("*.jar");
        } catch (SftpException s) {
            return new Vector();
        }
    }

}
