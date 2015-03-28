package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.protocol.ssh.SSHHandler;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.module.Module;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class RaspberryPIRunner extends DefaultProgramRunner {
    private static final String RUNNER_ID = "RaspberryPIRunner";
    private static final String[] OUTPUT_DIRECTORIES = {"out", "target"};

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState profileState, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        final RunProfile runProfileRaw = environment.getRunProfile();
        if (runProfileRaw instanceof RaspberryPIRunConfiguration) {
            RaspberryPIRunnerParameters runnerParameters = ((RaspberryPIRunConfiguration) runProfileRaw).getRunnerParameters();
            Module module = ((RaspberryPIRunConfiguration) runProfileRaw).getConfigurationModule().getModule();
            File outputDirectory = getOutputDirectory(module.getModuleFile().getParent().getPath());
            try {
                buildSshClient(runnerParameters, outputDirectory);
            } catch (Exception e) {
            }
        }
        else {
            return super.doExecute(profileState, environment);
        }
        throw new NotImplementedException();
    }

    private File getOutputDirectory(String relPath) {
        //todo fix to come up with smart way to get output files
        for (String s : OUTPUT_DIRECTORIES) {
            File output = new File(relPath + File.separator + s);
            if (output.exists()) {
                return output;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return (DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) || DefaultRunExecutor.EXECUTOR_ID.equals(executorId)) &&
                profile instanceof RaspberryPIRunConfiguration;
    }

    private void buildSshClient(RaspberryPIRunnerParameters rp, File outputDirectory) throws RuntimeConfigurationException, IOException, ClassNotFoundException {
            SSHHandler sshHandler = new SSHHandler(rp.getHostname(), rp.getUsername(), rp.getPassword());
        sshHandler.upload(outputDirectory);
    }
}
