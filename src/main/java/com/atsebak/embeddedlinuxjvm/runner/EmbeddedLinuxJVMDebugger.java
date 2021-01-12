package com.atsebak.embeddedlinuxjvm.runner;

import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMToolWindowFactory;
import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
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

public class EmbeddedLinuxJVMDebugger extends GenericDebuggerRunner {


    private static final String RUNNER_ID = "RaspberryPIDebugger";

    /**
     * Constructor
     */
    public EmbeddedLinuxJVMDebugger() {
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
        return (DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof EmbeddedLinuxJVMRunConfiguration);
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
        if (runProfileRaw instanceof EmbeddedLinuxJVMRunConfiguration) {
            FileDocumentManager.getInstance().saveAllDocuments();
            setupConsole(environment.getProject());
            super.doExecute(project, state, contentToReuse, environment);
        }
        return super.doExecute(project, state, contentToReuse, environment);
    }

    /**
     * Adds a Console Logger From The Remote App
     *
     * @param p
     */
    private void setupConsole(Project p) {
        ToolWindow window = ToolWindowManager.getInstance(p).getToolWindow(EmbeddedLinuxJVMToolWindowFactory.ID);
        if (window != null) {
            window.activate(null, true);
            EmbeddedLinuxJVMConsoleView.getInstance(p).clear();
        }
    }
}
