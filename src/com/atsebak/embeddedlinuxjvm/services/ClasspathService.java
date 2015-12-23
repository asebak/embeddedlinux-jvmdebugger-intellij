package com.atsebak.embeddedlinuxjvm.services;


import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSHHandlerTarget;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.EmbeddedSSHClient;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
     * Note: This is not a proper way of doing this but simple to do
     * @param hostLibraries
     * @return targetLibraries
     */
    @Deprecated
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
     * Proper way: Gets the deployed jars on target machine using SSH than compares name, date and size to make sure its correct
     * The reason for this as it will exponentially increase deployment times to not keep on uploading files that already exist on remote.
     * @return
     * @throws IOException
     * @throws RuntimeConfigurationException
     * @deprecated use deltaOfDeployedJars instead
     */
    @Deprecated
    public List<File> invokeFindDeployedJars(List<File> hostLibraries, EmbeddedLinuxJVMRunConfigurationRunnerParameters runnerParameters)
            throws IOException, RuntimeConfigurationException, JSchException, SftpException {
        SSHHandlerTarget target = SSHHandlerTarget.builder().params(runnerParameters)
                .consoleView(EmbeddedLinuxJVMConsoleView.getInstance(project))
                .ssh(EmbeddedSSHClient.builder()
                        .hostname(runnerParameters.getHostname())
                        .password(runnerParameters.getPassword())
                        .username(runnerParameters.getUsername())
                        .useKey(runnerParameters.isUsingKey())
                        .key(runnerParameters.getKeyPath())
                        .build())
                .build();
        Set<String> filesAlreadyDeployed = new HashSet<String>();
        List<File> newLibraries = new ArrayList<File>();
        Vector alreadyDeployedLibraries = target.getAlreadyDeployedLibraries();
        for (Object alreadyDeployedLibrary : alreadyDeployedLibraries) {
            ChannelSftp.LsEntry targetLibrary = (ChannelSftp.LsEntry) alreadyDeployedLibrary;
            for (File hostFile : hostLibraries) {
                //Names, Last Modified and Size have to be the exact same for it to be identied as the same file
                if (targetLibrary.getFilename().equals(hostFile.getName()) && hostFile.length() == targetLibrary.getAttrs().getSize()
                        && targetLibrary.getAttrs().getMtimeString().equals(new Date(hostFile.lastModified()).toString())) {
                    //hash what files exist
                    filesAlreadyDeployed.add(hostFile.getName());
                    break;
                }
            }
        }

        //add files that do not exist on target
        for (File hostFile : hostLibraries) {
            if (!filesAlreadyDeployed.contains(hostFile.getName())) {
                newLibraries.add(hostFile);
            }
        }
        return newLibraries;
    }
}
