package com.atsebak.raspberrypi.console;

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
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class PIConsoleView implements Disposable {
    private static final Class<?>[] IGNORED_CONSOLE_ACTION_TYPES =
            {PreviousOccurenceToolbarAction.class, NextOccurenceToolbarAction.class, ConsoleViewImpl.ClearAllAction.class, PrintAction.class};

    @NotNull
    private final Project myProject;
    @NotNull
    private final ConsoleViewImpl myConsoleView;

    private JPanel myConsolePanel = new JPanel();

    public PIConsoleView(@NotNull Project project) {
        myProject = project;
        myConsoleView = new ConsoleViewImpl(myProject, false);
        Disposer.register(this, myConsoleView);
    }

    public static PIConsoleView getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, PIConsoleView.class);
    }

    private static boolean shouldIgnoreAction(@NotNull AnAction action) {
        for (Class<?> actionType : IGNORED_CONSOLE_ACTION_TYPES) {
            if (actionType.isInstance(action)) {
                return true;
            }
        }
        return false;
    }

    public void createToolWindowContent(@NotNull ToolWindow toolWindow) {
        //Create runner UI layout
        RunnerLayoutUi.Factory factory = RunnerLayoutUi.Factory.getInstance(myProject);
        RunnerLayoutUi layoutUi = factory.create("", "", "session", myProject);

        // Adding actions
        DefaultActionGroup group = new DefaultActionGroup();
        layoutUi.getOptions().setLeftToolbar(group, ActionPlaces.UNKNOWN);

        Content console = layoutUi.createContent(PIConsoleToolWindowFactory.ID, myConsoleView.getComponent(), "", null, null);
        AnAction[] consoleActions = myConsoleView.createConsoleActions();
        for (AnAction action : consoleActions) {
            if (!shouldIgnoreAction(action)) {
                group.add(action);
            }
        }
        layoutUi.addContent(console, 0, PlaceInGrid.right, false);

        JComponent layoutComponent = layoutUi.getComponent();
        myConsolePanel.add(layoutComponent, BorderLayout.CENTER);

        //noinspection ConstantConditions
        Content content = ContentFactory.SERVICE.getInstance().createContent(layoutComponent, null, true);
        toolWindow.getContentManager().addContent(content);
    }

    public void clear() {
        if (myConsoleView.isShowing()) {
            myConsoleView.clear();
        } else {
            // "clear" does not work when the console is not visible. We need to flush the text from previous sessions. It has to be done in the
            // UI thread, but we cannot call "invokeLater" because it will delete text belonging to the current session.
            ApplicationManager.getApplication().invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    // "flushDeferredText" is really "delete text from previous sessions and leave the text of the current session untouched."
                    myConsoleView.flushDeferredText();
                }
            }, ModalityState.NON_MODAL);
        }
    }

    public void print(@NotNull String text, @NotNull ConsoleViewContentType contentType) {
        myConsoleView.print(text, contentType);
    }

    @Override
    public void dispose() {
    }
}
