package com.atsebak.embeddedlinuxjvm.services;


import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.deploy.DeployedLibrary;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSH;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSHHandlerTarget;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClasspathService {
    @NotNull
    private final Project project;
    private List<File> previousDeployFiles;

    /**
     * Clear up static file list of deployments
     */
    public ClasspathService(@NotNull Project project) {
        this.project = project;
        previousDeployFiles = new ArrayList<File>();
    }

    /**
     * Gets the delta of jars between host and target machines.  It will always add the normal project output path but not necessarily the external libs
     * Some potential problems can be that the user stops it midway while deploying so the jars don't go on the target.
     *
     * @param hostLibraries
     * @return targetLibraries
     */
    public List<File> deltaOfDeployedJars(List<File> hostLibraries) throws IOException, RuntimeConfigurationException {
        List<File> newLibraries = new ArrayList<File>();
        for (File hostFile : hostLibraries) {
            if (!hostFile.getName().contains("jar")) {
                newLibraries.add(hostFile);
            } else if (!previousDeployFiles.contains(hostFile)) {
                newLibraries.add(hostFile);
            }
        }
        previousDeployFiles = hostLibraries;
        return newLibraries;
    }

    /**
     * Gets the deployed jars on target machine
     *
     * @return
     * @throws IOException
     * @throws RuntimeConfigurationException
     * @deprecated use deltaOfDeployedJars instead
     */
    @Deprecated
    public List<DeployedLibrary> invokeFindDeployedJars(EmbeddedLinuxJVMRunConfigurationRunnerParameters runnerParameters)
            throws IOException, RuntimeConfigurationException {
        SSHHandlerTarget target = SSHHandlerTarget.builder().piRunnerParameters(runnerParameters)
                .consoleView(EmbeddedLinuxJVMConsoleView.getInstance(project))
                .ssh(SSH.builder()
                        .connectionTimeout(30000)
                        .timeout(30000)
                        .build()).build();
        return target.listAlreadyUploadedJars();
    }
}
