package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.intellij.openapi.project.Project;
import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SFTPHandler {
    private Project project;

    public SFTPHandler(Project project) {
        this.project = project;
    }

    public void upload(Session session, final File upload, String deploymentPath) throws JSchException, SftpException, FileNotFoundException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        final ChannelSftp channelSftp = (ChannelSftp) channel;
        channelSftp.cd(deploymentPath);
        channelSftp.put(new FileInputStream(upload), upload.getName(), new SFTPProgress());
    }


}
