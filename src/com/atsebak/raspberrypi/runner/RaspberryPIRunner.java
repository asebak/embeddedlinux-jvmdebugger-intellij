package com.atsebak.raspberrypi.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.remote.RemoteConfiguration;
import com.intellij.execution.remote.RemoteConfigurationType;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class RaspberryPIRunner extends DefaultProgramRunner {
    private static final String RUNNER_ID = "RaspberryPIRunner";
    @NonNls
    private static final String RUN_CONFIGURATION_NAME_PATTERN = "PI Debugger (%s)";

    @NotNull
    private static String getRunConfigurationName(String debugPort) {
        return String.format(RUN_CONFIGURATION_NAME_PATTERN, debugPort);
    }

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
            RaspberryPIRunnerParameters runnerParameters = ((RaspberryPIRunConfiguration) runProfileRaw).getRunnerParameters();
            final RunnerAndConfigurationSettings settings = createRunConfiguration(environment.getProject(),
                    runnerParameters.getPort(), runnerParameters.getHostname());
            ProgramRunnerUtil.executeConfiguration(environment.getProject(), settings, DefaultDebugExecutor.getDebugExecutorInstance());
            return null;
//            return super.doExecute(profileState, environment);
        } else {
            return super.doExecute(profileState, environment);
        }
//        else {
//            return super.doExecute(profileState, environment);
//        }
//        return super.doExecute(profileState, environment);
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

    private RunnerAndConfigurationSettings createRunConfiguration(Project project, String debugPort, String hostname) {
        final RemoteConfigurationType remoteConfigurationType = RemoteConfigurationType.getInstance();

        final ConfigurationFactory factory = remoteConfigurationType.getFactory();
        final RunnerAndConfigurationSettings runSettings =
                RunManager.getInstance(project).createRunConfiguration(getRunConfigurationName(debugPort), factory);
        final RemoteConfiguration configuration = (RemoteConfiguration) runSettings.getConfiguration();

        configuration.HOST = hostname;
        configuration.PORT = debugPort;
        configuration.USE_SOCKET_TRANSPORT = true;
        configuration.SERVER_MODE = false;

        return runSettings;
    }

}
