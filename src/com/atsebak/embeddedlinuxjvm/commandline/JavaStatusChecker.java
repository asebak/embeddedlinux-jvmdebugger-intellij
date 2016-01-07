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
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

public class JavaStatusChecker extends Thread {
    private final ChannelExec channelExec;
    private final Project project;
    private final EmbeddedLinuxJVMConsoleView consoleView;

    public JavaStatusChecker(@NotNull EmbeddedLinuxJVMConsoleView consoleView) {
        this.consoleView = consoleView;
        project = null;
        channelExec = null;
    }
    public JavaStatusChecker(@Nullable ChannelExec channelExec, @NotNull EmbeddedLinuxJVMConsoleView consoleView) {
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
        if (channelExec == null) {
            return;
        }
        while (!channelExec.isClosed()) {
            Thread.sleep(1000);
        }
        stopApplication(channelExec.getExitStatus());
        channelExec.disconnect();
        channelExec.getSession().disconnect();
    }

    /**
     * Stops java application that needs to, this should be called when the user wants to manually stop the application
     * so that it kills the remote java process
     *
     * @param session
     * @param isRunningAsRoot
     * @param mainClass
     * @throws JSchException
     * @throws IOException
     */
    public void forceStopJavaApplication(@Nullable Session session, @NotNull boolean isRunningAsRoot, @NotNull String mainClass) throws JSchException, IOException {
        if (session == null) {
            return;
        }
        String javaKillCmd = String.format("%s kill -9 $(ps -efww | grep \"%s\"| grep -v grep | tr -s \" \"| cut -d\" \" -f2)",
                isRunningAsRoot ? "sudo" : "", mainClass);
        Channel channel = session.openChannel("shell");

        OutputStream inputstream_for_the_channel = channel.getOutputStream();
        PrintStream commander = new PrintStream(inputstream_for_the_channel, true);

        channel.connect();

        commander.println(javaKillCmd);
        commander.close();
        channel.disconnect();
        session.disconnect();
    }

    /**
     * Stops the applications via the descriptor of configuration.  This gets called when the application finishes on the client side without maniually closing it.
     *
     * @param javaExitCode
     */
    public void stopApplication(@NotNull int javaExitCode) {
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
