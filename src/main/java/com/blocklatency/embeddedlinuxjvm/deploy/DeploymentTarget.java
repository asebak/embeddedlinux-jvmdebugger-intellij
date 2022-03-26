package com.blocklatency.embeddedlinuxjvm.deploy;

import com.blocklatency.embeddedlinuxjvm.protocol.ssh.SSHHandlerTarget;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

@Builder
public class DeploymentTarget {
    private final SSHHandlerTarget sshHandlerTarget;

    /**
     * Uploads to the embedded system for a given Java Project storing it in /home/{username}/IdeaProjects
     *
     * @param outputDirectory Full of output from compiling
     * @param cmd             Java Command
     * @throws RuntimeConfigurationException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void upload(@NotNull final File outputDirectory, @NotNull final String cmd) throws RuntimeConfigurationException, IOException, ClassNotFoundException {
        sshHandlerTarget.uploadAndRunJavaApp(outputDirectory, cmd);
    }

    /**
     * Uploads any type of directory/file to the given path the user chose.
     *
     * @param uploadTo
     * @param fileToUpload
     */
    public void upload(@NotNull final String uploadTo, @NotNull final File fileToUpload) throws IOException, RuntimeConfigurationException {
        sshHandlerTarget.genericUpload(uploadTo, fileToUpload);
    }
}
