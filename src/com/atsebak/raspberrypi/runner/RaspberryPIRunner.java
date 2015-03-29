package com.atsebak.raspberrypi.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;

public class RaspberryPIRunner extends DefaultProgramRunner {
    private static final String RUNNER_ID = "RaspberryPIRunner";

    /**
     * Executes the Runner, This only gets called in debug mode
     *
     * @param profileState
     * @param environment
     * @return
     * @throws ExecutionException
     */
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState profileState, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        final RunProfile runProfileRaw = environment.getRunProfile();
        if (runProfileRaw instanceof RaspberryPIRunConfiguration) {

        }
        else {
            return super.doExecute(profileState, environment);
        }
        return super.doExecute(profileState, environment);
    }

    /**
     * Gets the active runner id
     *
     * @return
     */
    @NotNull
    @Override
    public String getRunnerId() {
        return RUNNER_ID;
    }

    /**
     * Method is constantly called but is always false unless user invokes it from IDEA
     * @param executorId
     * @param profile
     * @return
     */
    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return (DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) || DefaultRunExecutor.EXECUTOR_ID.equals(executorId)) &&
                profile instanceof RaspberryPIRunConfiguration;
    }

}
