package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.intellij.openapi.wm.StatusBar;
import com.jcraft.jsch.SftpProgressMonitor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SFTPProgress implements SftpProgressMonitor {
    private ProgressMonitor monitor;
    private StatusBar statusBar;
    private long count = 0;
    private long max = 0;
    private float percent;

    public SFTPProgress(@NotNull StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        statusBar.setInfo(EmbeddedLinuxJVMBundle.getString("pi.upload") + " 0%");
        this.max = max;
        monitor = new ProgressMonitor(null, ((op == SftpProgressMonitor.PUT) ? "put" : "get") + ": " + src, "", 0, (int) max);
        count = 0;
        percent = -1;
        monitor.setProgress((int) this.count);
        monitor.setMillisToDecideToPopup(1000);
    }

    @Override
    public boolean count(long count) {
        this.count += count;
        if (percent >= this.count * 100 / max) {
            return true;
        }
        percent = this.count * 100 / max;
        monitor.setNote("Completed " + this.count + "(" + percent + "%) out of " + max + ".");
        statusBar.setInfo(EmbeddedLinuxJVMBundle.getString("pi.upload") + this.count + "(" + percent + "%) out of " + max + ".");
        monitor.setProgress((int) this.count);

        return !(monitor.isCanceled());
    }

    @Override
    public void end() {
        statusBar.setInfo("");
        monitor.close();
    }
}
