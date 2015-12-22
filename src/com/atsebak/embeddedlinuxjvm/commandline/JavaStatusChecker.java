package com.atsebak.embeddedlinuxjvm.commandline;


import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMConfigurationType;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.jcraft.jsch.ChannelExec;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.List;

public class JavaStatusChecker extends Thread {
    private final ChannelExec channelExec;
    private final Project project;
    private final EmbeddedLinuxJVMRunConfigurationRunnerParameters parameters;

    public JavaStatusChecker(ChannelExec channelExec, Project project, EmbeddedLinuxJVMRunConfigurationRunnerParameters parameters) {
        this.channelExec = channelExec;
        this.project = project;
        this.parameters = parameters;
    }


    @Override
    @SneakyThrows
    public void run() {
        while (!channelExec.isClosed()) { //runs until java app closes from target device
            Thread.sleep(1000);
        }
        channelExec.disconnect();
        channelExec.getSession().disconnect();
        //stopApplication(); //todo fix this stops debugger and runner states
    }

    private void stopApplication() {
        final RunManagerImpl runManager = (RunManagerImpl) RunManager.getInstance(project);
        final Collection<RunnerAndConfigurationSettings> allConfigurations = runManager.getSortedConfigurations();
        List<RunContentDescriptor> allDescriptors = ExecutionManager.getInstance(project).getContentManager().getAllDescriptors();

        for (RunnerAndConfigurationSettings runConfiguration : allConfigurations) {
            if (runConfiguration.getConfiguration().getFactory().getType() instanceof EmbeddedLinuxJVMConfigurationType) {
                for (RunContentDescriptor descriptor : allDescriptors) {
                    if (runConfiguration.getName().equals(descriptor.getDisplayName())) {
                        try {
                            descriptor.getProcessHandler().destroyProcess();
                        } catch (Exception e) {

                        }
                    }
                }

            }

        }
    }

}
