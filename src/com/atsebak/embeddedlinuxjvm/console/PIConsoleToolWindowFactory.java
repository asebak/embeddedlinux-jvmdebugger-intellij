package com.atsebak.embeddedlinuxjvm.console;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;


public class PIConsoleToolWindowFactory implements ToolWindowFactory, DumbAware {
    public static final String ID = "PI Console";

    /**
     * Creates a custom tool window show pi logs (might be temporary)
     *
     * @param project
     * @param toolWindow
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        PIConsoleView.getInstance(project).createToolWindowContent(toolWindow);
    }
}
