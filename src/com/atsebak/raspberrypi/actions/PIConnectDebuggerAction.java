package com.atsebak.raspberrypi.actions;

import com.atsebak.raspberrypi.ui.PIProcessChooserDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;

public class PIConnectDebuggerAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        assert project != null;

        final PIProcessChooserDialog dialog = new PIProcessChooserDialog(project);
//        dialog.show();
    }


    @Override
    public void update(AnActionEvent e) {
        super.update(e);
//        final Project project = e.getData(CommonDataKeys.PROJECT);
//        e.getPresentation().setVisible(project != null &&
//                ProjectFacetManager.getInstance(project).getFacets(AndroidFacet.ID).size() > 0);
    }
}
