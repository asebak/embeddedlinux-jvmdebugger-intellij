package com.atsebak.embeddedlinuxjvm.commandline;


import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMConfigurationType;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.jcraft.jsch.ChannelExec;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class JavaStatusChecker extends Thread {
    private final ChannelExec channelExec;
    private final Project project;
    private final EmbeddedLinuxJVMConsoleView consoleView;

    public JavaStatusChecker(@NotNull ChannelExec channelExec, @NotNull EmbeddedLinuxJVMConsoleView consoleView) {
        this.channelExec = channelExec;
        this.project = consoleView.getProject();
        this.consoleView = consoleView;
    }

    /**
     * Runs until java app finishes from target device
     */
    @Override
    @SneakyThrows
    public void run() {
        while (!channelExec.isClosed()) {
            Thread.sleep(1000);
        }
        stopApplication(channelExec.getExitStatus());
        channelExec.disconnect();
        channelExec.getSession().disconnect();
    }

    /**
     * Stops the applications via the descriptor of configuration
     *
     * @param javaExitCode
     */
    private void stopApplication(@NotNull int javaExitCode) {
        final RunManagerImpl runManager = (RunManagerImpl) RunManager.getInstance(project);
        final Collection<RunnerAndConfigurationSettings> allConfigurations = runManager.getSortedConfigurations();
        List<RunContentDescriptor> allDescriptors = ExecutionManager.getInstance(project).getContentManager().getAllDescriptors();
        boolean exitMsgDisplay = false;
        for (RunnerAndConfigurationSettings runConfiguration : allConfigurations) {
            if (runConfiguration.getConfiguration().getFactory().getType() instanceof EmbeddedLinuxJVMConfigurationType) {
                for (RunContentDescriptor descriptor : allDescriptors) {
                    if (runConfiguration.getName().equals(descriptor.getDisplayName())) {
                        try {
                            if (!exitMsgDisplay) {
                                consoleView.print(EmbeddedLinuxJVMBundle.message("exit.code.message", javaExitCode), ConsoleViewContentType.SYSTEM_OUTPUT);
                                exitMsgDisplay = true;
                            }
                            descriptor.setProcessHandler(null);
                        } catch (Exception e) {

                        }
                    }
                }

            }

        }
    }
}
