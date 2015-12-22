package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class IndicatorComponent implements StatusBarWidget, StatusBarWidget.IconPresentation {
    private StatusBar myStatusBar;

    private Icon myCurrentIcon = AllIcons.Ide.IncomingChangesOff;
    private String myToolTipText;

    public IndicatorComponent() {
    }

    public void clear() {
        update(AllIcons.Ide.IncomingChangesOff, "No incoming changelists available");
    }

    public void setChangesAvailable(@NotNull final String toolTipText) {
        update(AllIcons.Ide.Info_notifications, toolTipText);
    }

    private void update(@NotNull final Icon icon, @Nullable final String toolTipText) {
        myCurrentIcon = icon;
        myToolTipText = toolTipText;
        if (myStatusBar != null) myStatusBar.updateWidget(ID());
    }

    @NotNull
    public Icon getIcon() {
        return myCurrentIcon;
    }

    public String getTooltipText() {
        return myToolTipText;
    }

    public Consumer<MouseEvent> getClickConsumer() {
        return new Consumer<MouseEvent>() {
            public void consume(final MouseEvent mouseEvent) {
                if (myStatusBar != null) {
                    DataContext dataContext = DataManager.getInstance().getDataContext((Component) myStatusBar);
                    final Project project = CommonDataKeys.PROJECT.getData(dataContext);
                    if (project != null) {
                        ToolWindow changesView = ToolWindowManager.getInstance(project).getToolWindow(ChangesViewContentManager.TOOLWINDOW_ID);
                        changesView.show(new Runnable() {
                            public void run() {
                                ChangesViewContentManager.getInstance(project).selectContent("Incoming");
                            }
                        });
                    }
                }
            }
        };
    }

    @NotNull
    public String ID() {
        return "IncomingChanges";
    }

    public WidgetPresentation getPresentation(@NotNull PlatformType type) {
        return this;
    }

    public void install(@NotNull StatusBar statusBar) {
        myStatusBar = statusBar;
    }

    public void dispose() {
        myStatusBar = null;
    }
}
