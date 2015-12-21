package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SFTPHandler {
    public void upload(Session session, File upload, String deploymentPath) throws JSchException, SftpException, FileNotFoundException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp channelSftp = (ChannelSftp) channel;
        channelSftp.cd(deploymentPath);
        channelSftp.put(new FileInputStream(upload), upload.getName(), ChannelSftp.OVERWRITE);
    }

}
