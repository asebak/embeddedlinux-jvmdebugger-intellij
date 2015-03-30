package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.runner.data.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.IOException;

public class SSHUploader {

    private final Project project;

    public SSHUploader(Project project) {
        this.project = project;
    }

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
        SSHHandler sshHandler = new SSHHandler(project, rp);
        sshHandler.upload(outputDirectory, cmd);
    }
}


