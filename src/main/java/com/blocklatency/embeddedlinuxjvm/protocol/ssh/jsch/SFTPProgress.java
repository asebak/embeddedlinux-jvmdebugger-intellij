package com.blocklatency.embeddedlinuxjvm.protocol.ssh.jsch;


import com.blocklatency.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.blocklatency.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.blocklatency.embeddedlinuxjvm.protocol.ssh.SSHHandlerTarget;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.jcraft.jsch.SftpProgressMonitor;
import org.jetbrains.annotations.NotNull;

public class SFTPProgress implements SftpProgressMonitor {
    private double count;
    private double max;
    private int percent;
    private EmbeddedLinuxJVMConsoleView consoleView;
    private int lastDisplayedPercent;

    public SFTPProgress(@NotNull EmbeddedLinuxJVMConsoleView consoleView) {
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

    /**
     * Progress of upload/download
     *
     * @param count
     * @return
     */
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
