package com.atsebak.raspberrypi.deploy;

import com.atsebak.raspberrypi.protocol.ssh.SSHHandlerTarget;
import com.atsebak.raspberrypi.runner.data.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

@Builder
public class DeploymentTarget {
    private final Project project;
    private final RaspberryPIRunnerParameters rp;

    /**
     * Gets SSh Handler by Building it
     *
     * @return
     */
    private SSHHandlerTarget getSSHHandler() {
        return SSHHandlerTarget.builder().project(project).piRunnerParameters(rp).build();
    }

    /**
     * Uploads to the embedded system for a given Java Project storing it in /home/{username}/IdeaProjects
     *
     * @param outputDirectory Full of output from compiling
     * @param cmd             Java Command
     * @throws RuntimeConfigurationException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void upload(final File outputDirectory, final String cmd) throws RuntimeConfigurationException, IOException, ClassNotFoundException {
        getSSHHandler().uploadAndRunJavaApp(outputDirectory, cmd);
    }

    /**
     * Uploads any type of directory/file to the given path the user chose.
     *
     * @param uploadTo
     * @param fileToUpload
     */
    public void upload(@NotNull final String uploadTo, @NotNull final File fileToUpload) throws IOException, RuntimeConfigurationException {
        getSSHHandler().genericUpload(uploadTo, fileToUpload);
    }
}
