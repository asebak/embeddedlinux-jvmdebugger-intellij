package com.atsebak.embeddedlinuxjvm.console;

import com.intellij.codeEditor.printing.PrintAction;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.ide.actions.NextOccurenceToolbarAction;
import com.intellij.ide.actions.PreviousOccurenceToolbarAction;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class EmbeddedLinuxJVMConsoleView implements Disposable {
    private static final Class<?>[] IGNORED_CONSOLE_ACTION_TYPES =
            {PreviousOccurenceToolbarAction.class, NextOccurenceToolbarAction.class, ConsoleViewImpl.ClearAllAction.class, PrintAction.class};

    @NotNull
    private final Project project;

    @Nullable
    private Session.Command command;

    @NotNull
    private ConsoleViewImpl consoleView;

    @NotNull
    private JPanel myConsolePanel = new JPanel();

    /**
     * Gets the Console View
     * @return
     */
    @NotNull
    public ConsoleViewImpl getConsoleView(boolean isNew) {
        if(isNew) {
            consoleView = new ConsoleViewImpl(project, false);
        }
        return consoleView;
    }

    public EmbeddedLinuxJVMConsoleView(@NotNull Project project) {
        this.project = project;
        consoleView = new ConsoleViewImpl(project, false);
        Disposer.register(this, consoleView);
    }

    /**
     * Signleton Instance for The Pi Console view
     *
     * @param project
     * @return
     */
    public static EmbeddedLinuxJVMConsoleView getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, EmbeddedLinuxJVMConsoleView.class);
    }

    /**
     * Should Ignore?
     *
     * @param action
     * @return
     */
    private static boolean shouldIgnoreAction(@NotNull AnAction action) {
        for (Class<?> actionType : IGNORED_CONSOLE_ACTION_TYPES) {
            if (actionType.isInstance(action)) {
                return true;
            }
        }
        return false;
    }

    public Project getProject() {
        return project;
    }

    /**
     * Creats the tool window content
     * @param toolWindow
     */
    public void createToolWindowContent(@NotNull ToolWindow toolWindow) {
        //Create runner UI layout
        RunnerLayoutUi.Factory factory = RunnerLayoutUi.Factory.getInstance(project);
        RunnerLayoutUi layoutUi = factory.create("", "", "session", project);

        // Adding actions
        DefaultActionGroup group = new DefaultActionGroup();
        layoutUi.getOptions().setLeftToolbar(group, ActionPlaces.UNKNOWN);

        Content console = layoutUi.createContent(EmbeddedLinuxJVMToolWindowFactory.ID, consoleView.getComponent(), "", null, null);
        AnAction[] consoleActions = consoleView.createConsoleActions();
        for (AnAction action : consoleActions) {
            if (!shouldIgnoreAction(action)) {
                group.add(action);
            }
        }
        layoutUi.addContent(console, 0, PlaceInGrid.right, false);

        JComponent layoutComponent = layoutUi.getComponent();
        myConsolePanel.add(layoutComponent, BorderLayout.CENTER);
        Content content = ContentFactory.SERVICE.getInstance().createContent(layoutComponent, null, true);
        toolWindow.getContentManager().addContent(content);
    }

    /**
     * Clears text on console
     */
    public void clear() {
        if (consoleView.isShowing()) {
            consoleView.clear();
        } else {
            ApplicationManager.getApplication().invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    consoleView.flushDeferredText();
                }
            }, ModalityState.NON_MODAL);
        }
    }

    /**
     * Prints on the Console
     * @param text
     * @param contentType
     */
    public void print(@NotNull String text, @NotNull ConsoleViewContentType contentType) {
        consoleView.print(text, contentType);
    }

    /**
     * Dispose register
     */
    @Override
    public void dispose() {
    }

    /**
     * Get Command
     * @return
     */
    @Nullable
    public Session.Command getCommand() {
        return command;
    }

    /**
     * Set command
     * @param command
     */
    public void setCommand(@Nullable Session.Command command) {
        this.command = command;
    }
}
