package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.intellij.openapi.project.Project;
import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileNotFoundException;

public class SFTPHandler {
    private Project project;
    private EmbeddedLinuxJVMConsoleView consoleView;

    public SFTPHandler(EmbeddedLinuxJVMConsoleView consoleView) {
        this.consoleView = consoleView;
        this.project = consoleView.getProject();
    }

    public void upload(Session session, final File upload, String deploymentPath) throws JSchException, SftpException, FileNotFoundException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        final ChannelSftp channelSftp = (ChannelSftp) channel;
        channelSftp.cd(deploymentPath);
        channelSftp.put(upload.getAbsolutePath(), upload.getName(), new SFTPProgress(consoleView), ChannelSftp.OVERWRITE);
    }

}
