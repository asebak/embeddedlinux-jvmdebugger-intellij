package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.console.PIConsoleToolWindowFactory;
import com.atsebak.raspberrypi.console.PIConsoleView;
import com.atsebak.raspberrypi.runner.conf.RaspberryPIRunConfiguration;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

public class RaspberryPIDebugger extends GenericDebuggerRunner {


    private static final String RUNNER_ID = "RaspberryPIDebugger";

    /**
     * Constructor
     */
    public RaspberryPIDebugger() {
        super();
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
            setupConsole(environment.getProject());
            super.doExecute(project, state, contentToReuse, environment);
        }
        return null;
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
