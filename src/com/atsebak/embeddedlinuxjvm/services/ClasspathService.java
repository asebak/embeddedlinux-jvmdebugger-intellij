package com.atsebak.embeddedlinuxjvm.services;


import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.deploy.DeployedLibrary;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSHHandlerTarget;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.EmbeddedSSHClient;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClasspathService {
    @NotNull
    private final Project project;
    private List<File> previousLibraries;

    /**
     * IntelliJ Service Injected
     */
    public ClasspathService(@NotNull Project project) {
        this.project = project;
        previousLibraries = new ArrayList<File>();
    }

    /**
     * Gets the delta of jars between host and target machines.  It will always add the normal project output path but not necessarily the external libs
     * Some potential problems can be that the user stops it midway while deploying so the jars don't go on the target.
     *
     * @param hostLibraries
     * @return targetLibraries
     */
    public List<File> deltaOfDeployedJars(List<File> hostLibraries) {
        List<File> targetLibraries = new ArrayList<File>();
        for (File hostFile : hostLibraries) {
            if (!hostFile.getName().contains("jar")) {
                targetLibraries.add(hostFile);
            } else if (!previousLibraries.contains(hostFile)) {
                targetLibraries.add(hostFile);
            }
        }
        previousLibraries = hostLibraries;
        return targetLibraries;
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
    public List<File> invokeFindDeployedJars(List<File> hostLibraries, EmbeddedLinuxJVMRunConfigurationRunnerParameters runnerParameters)
            throws IOException, RuntimeConfigurationException {
        SSHHandlerTarget target = SSHHandlerTarget.builder().piRunnerParameters(runnerParameters)
                .consoleView(EmbeddedLinuxJVMConsoleView.getInstance(project))
                .ssh(EmbeddedSSHClient.builder()
                        .hostname(runnerParameters.getHostname())
                        .password(runnerParameters.getPassword())
                        .username(runnerParameters.getUsername()).build())
                .build();

//        List<DeployedLibrary> targetLibraries = target.listAlreadyUploadedJars(); //todo fix but already deprecated
        List<DeployedLibrary> targetLibraries = null;

        Set<String> filesToDeploy = new HashSet<String>(); //hash what files exist
        //todo improve based on last modified date, size, and the name of the file and not just the filename
        for (DeployedLibrary deployedLibrary : targetLibraries) {
            for (File hostFile : hostLibraries) {
                if (deployedLibrary.getJarName().equals(hostFile.getName())) {
                    filesToDeploy.add(hostFile.getName());
                    break;
                }
            }
        }

        //add files that do not exist on target
        List<File> newLibraries = new ArrayList<File>();
        for (File hostFile : hostLibraries) {
            if (!filesToDeploy.contains(hostFile.getName())) {
                newLibraries.add(hostFile);
            }
        }
        return newLibraries;
    }
}
