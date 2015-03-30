package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.runner.data.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import lombok.Builder;

import java.io.File;
import java.io.IOException;

@Builder
public class SSHUploader {
    private final Project project;
    private final RaspberryPIRunnerParameters rp;
    /**
     * Uploads to the embedded system
     *
     * @param outputDirectory
     * @param cmd
     * @throws RuntimeConfigurationException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void uploadToTarget(final File outputDirectory, final String cmd)
            throws RuntimeConfigurationException, IOException, ClassNotFoundException {
        SSHHandler handler = SSHHandler.builder().project(project).piRunnerParameters(rp).build();
        handler.upload(outputDirectory, cmd);
    }
}


