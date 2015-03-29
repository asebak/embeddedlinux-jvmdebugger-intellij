package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.runner.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;

import java.io.File;
import java.io.IOException;

public class SSHUploader {
    /**
     * Uploads to the embedded system
     *
     * @param rp
     * @param outputDirectory
     * @param cmd
     * @throws RuntimeConfigurationException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void uploadToTarget(final RaspberryPIRunnerParameters rp, final File outputDirectory, final String cmd)
            throws RuntimeConfigurationException, IOException, ClassNotFoundException {
        SSHHandler sshHandler = new SSHHandler(rp.getHostname(), rp.getUsername(), rp.getPassword());
        sshHandler.upload(outputDirectory, cmd);
    }
}


