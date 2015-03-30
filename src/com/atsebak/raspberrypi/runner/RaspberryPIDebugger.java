package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.console.PIConsoleToolWindowFactory;
import com.atsebak.raspberrypi.console.PIConsoleView;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.remote.RemoteConfiguration;
import com.intellij.execution.remote.RemoteConfigurationType;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RaspberryPIDebugger extends GenericDebuggerRunner {

    @NonNls
    private static final String RUN_CONFIGURATION_NAME_PATTERN = "PI Debugger (%s)";
    private static final String RUNNER_ID = "RaspberryPI";

    /**
     * Constructor
     */
    public RaspberryPIDebugger() {
        super();
    }

    /**
     * Gets the debug runner
     *
     * @param debugPort
     * @return
     */
    @NotNull
    private static String getRunConfigurationName(String debugPort) {
        return String.format(RUN_CONFIGURATION_NAME_PATTERN, debugPort);
    }

    /**
     * Gets the Runner Name
     *
     * @return
     */
    @NotNull
    public String getRunnerId() {
        return RUNNER_ID;
    }

    /**
     * This makes sure the Debug mode is executed and not run mode
     *
     * @param executorId
     * @param profile
     * @return
     */
    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return (DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof RaspberryPIRunConfiguration);
    }

    /**
     * Descritor for executor config
     *
     * @param state
     * @param env
     * @return
     * @throws ExecutionException
     */
    @Nullable
    @Override
    protected RunContentDescriptor createContentDescriptor(RunProfileState state, ExecutionEnvironment env)
            throws ExecutionException {
        JavaCommandLine javaCommandLine = (JavaCommandLine) state;
        JavaParameters params = javaCommandLine.getJavaParameters();

        return super.createContentDescriptor(state, env);
    }

    /**
     * Executes the runner
     *
     * @param project
     * @param state
     * @param contentToReuse
     * @param environment
     * @return
     * @throws ExecutionException
     */
    @Override
    protected RunContentDescriptor doExecute(@NotNull Project project, @NotNull RunProfileState state, RunContentDescriptor contentToReuse, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        final RunProfile runProfileRaw = environment.getRunProfile();
        if (runProfileRaw instanceof RaspberryPIRunConfiguration) {
            FileDocumentManager.getInstance().saveAllDocuments();
            RaspberryPIRunnerParameters parameters = ((RaspberryPIRunConfiguration) runProfileRaw).getRunnerParameters();
            closeOldSessionAndRun(environment.getProject(), parameters);
            setupConsole(environment.getProject());
        }
        return null;
    }

    /**
     * Creates debugging settings for server
     *
     * @param project
     * @param debugPort
     * @param hostname
     * @return
     */
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


    /**
     * Closes an old descriptor and creates a new one
     *
     * @param project
     * @param parameters
     */
    private void closeOldSessionAndRun(final Project project, RaspberryPIRunnerParameters parameters) {
        final String configurationName = getRunConfigurationName(parameters.getPort());
        final Collection<RunContentDescriptor> descriptors =
                ExecutionHelper.findRunningConsoleByTitle(project, new NotNullFunction<String, Boolean>() {
                    @NotNull
                    @Override
                    public Boolean fun(String title) {
                        return configurationName.equals(title);
                    }
                });

        if (descriptors.size() > 0) {
            final RunContentDescriptor descriptor = descriptors.iterator().next();
            final ProcessHandler processHandler = descriptor.getProcessHandler();
            final Content content = descriptor.getAttachedContent();

            if (processHandler != null && content != null) {
                final Executor executor = DefaultDebugExecutor.getDebugExecutorInstance();

                if (processHandler.isProcessTerminated()) {
                    ExecutionManager.getInstance(project).getContentManager()
                            .removeRunContent(executor, descriptor);
                } else {
                    content.getManager().setSelectedContent(content);
                    ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(executor.getToolWindowId());
                    window.activate(null, false, true);
                    return;
                }
            }
        }
        runSession(project, parameters);
    }

    /**
     * Runs in remote debug mode using that executioner
     *
     * @param project
     * @param parameters
     */
    private void runSession(final Project project, RaspberryPIRunnerParameters parameters) {
        final RunnerAndConfigurationSettings settings = createRunConfiguration(project, parameters.getPort(), parameters.getHostname());
        ProgramRunnerUtil.executeConfiguration(project, settings, DefaultDebugExecutor.getDebugExecutorInstance());
    }

    /**
     * Adds a Console Logger From The Remote App
     *
     * @param p
     */
    private void setupConsole(Project p) {
        ToolWindow window = ToolWindowManager.getInstance(p).getToolWindow(PIConsoleToolWindowFactory.ID);
        if (window != null) {
            window.activate(null, true);
            PIConsoleView.getInstance(p).clear();
        }
    }
}
