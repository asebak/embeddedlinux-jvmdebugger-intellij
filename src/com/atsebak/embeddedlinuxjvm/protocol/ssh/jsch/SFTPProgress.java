package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.jcraft.jsch.SftpProgressMonitor;

public class SFTPProgress implements SftpProgressMonitor {
    private double count;
    private double max;
    private int percent;
    private Project project;

    SFTPProgress(Project project) {
        this.project = project;
        count = 0;
        max = 0;
        percent = 0;
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        this.max = max;
        count = 0;
        percent = 0;
        showStatus();
    }

    @Override
    public boolean count(long count) {
        this.count += count;
        percent = (int) ((this.count / max) * 100.0);
        showStatus();
        return true;
    }

    @Override
    public void end() {
        WindowManager.getInstance().getStatusBar(project).setInfo("");
    }

    private void showStatus() {
        WindowManager.getInstance().getStatusBar(project).setInfo(EmbeddedLinuxJVMBundle.getString("pi.upload") + " " + percent + "%");
    }

}
