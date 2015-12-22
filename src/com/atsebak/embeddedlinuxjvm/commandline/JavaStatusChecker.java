package com.atsebak.embeddedlinuxjvm.commandline;


import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.openapi.project.Project;
import com.jcraft.jsch.ChannelExec;
import lombok.SneakyThrows;

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
        // todo programmatically end session based on weather debugging or running.
        channelExec.disconnect();
        channelExec.getSession().disconnect();
    }

}
