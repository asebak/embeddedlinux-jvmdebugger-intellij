package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.console.PIConsoleToolWindowFactory;
import com.atsebak.raspberrypi.console.PIConsoleView;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

public class RaspberryPIRunner extends DefaultProgramRunner {
    private static final String RUNNER_ID = "RaspberryPI";

    /**
     * Constructor
     */
    public RaspberryPIRunner() {
        super();
    }

    @Override
    protected void execute(@NotNull ExecutionEnvironment environment, Callback callback, @NotNull RunProfileState state) throws ExecutionException {
        return;
    }

    /**
     * Executes the Runner, This only gets called in run mode.
     * It returns null because you want to show only the PI Console
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
            FileDocumentManager.getInstance().saveAllDocuments();
            setupConsole(environment.getProject());
        }
        return null;
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
        return (DefaultRunExecutor.EXECUTOR_ID.equals(executorId)) && profile instanceof RaspberryPIRunConfiguration;
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
