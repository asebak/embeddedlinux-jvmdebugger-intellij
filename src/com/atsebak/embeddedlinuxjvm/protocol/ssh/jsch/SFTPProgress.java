package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSHHandlerTarget;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.jcraft.jsch.SftpProgressMonitor;

public class SFTPProgress implements SftpProgressMonitor {
    private double count;
    private double max;
    private int percent;
    private EmbeddedLinuxJVMConsoleView consoleView;
    private int lastDisplayedPercent;

    SFTPProgress(EmbeddedLinuxJVMConsoleView consoleView) {
        this.consoleView = consoleView;
        count = 0;
        max = 0;
        percent = 0;
        lastDisplayedPercent = 0;
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        this.max = max;
        count = 0;
        percent = 0;
        lastDisplayedPercent = 0;
        consoleView.print(EmbeddedLinuxJVMBundle.getString("pi.upload") + " 0%" + SSHHandlerTarget.NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
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
        consoleView.print(EmbeddedLinuxJVMBundle.getString("pi.deployment.finished") + SSHHandlerTarget.NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
//        WindowManager.getInstance().getStatusBar(consoleView.getProject()).setInfo("");
    }

    private void showStatus() {
        if (lastDisplayedPercent <= percent - 10) {
            consoleView.print(EmbeddedLinuxJVMBundle.getString("pi.upload") + " " + percent + "%" + SSHHandlerTarget.NEW_LINE, ConsoleViewContentType.SYSTEM_OUTPUT);
            lastDisplayedPercent = percent;
        }
//        WindowManager.getInstance().getStatusBar(consoleView.getProject()).setInfo(EmbeddedLinuxJVMBundle.getString("pi.upload") + " " + percent + "%");
    }

}
