package com.atsebak.embeddedlinuxjvm.protocol.ssh;

import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.intellij.execution.ui.ConsoleViewContentType;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.xfer.TransferListener;

import java.io.IOException;

public class SFTPListener implements TransferListener {
    private final EmbeddedLinuxJVMConsoleView consoleView;
    private final String relPath;

    /**
     * Overloaded constructor
     *
     * @param relPath
     * @param consoleView
     */
    public SFTPListener(String relPath, EmbeddedLinuxJVMConsoleView consoleView) {
        this.relPath = relPath;
        this.consoleView = consoleView;
    }

    /**
     * Directory
     * @param name
     * @return
     */
    public TransferListener directory(String name) {
        return new SFTPListener(this.relPath + name + "/", consoleView);
    }

    /**
     * Listener Event
     * @param name
     * @param size
     * @return
     */
    public StreamCopier.Listener file(String name, final long size) {
        consoleView.print(EmbeddedLinuxJVMBundle.getString("pi.upload"), ConsoleViewContentType.SYSTEM_OUTPUT);
//        StatusBar statusBar = WindowManager.getInstance().getStatusBar(consoleView.getProject());
//        final StatusBarProgress statusBarProgress = new StatusBarProgress();
//        statusBarProgress.start();
        return new StreamCopier.Listener() {
            @Override
            public void reportProgress(long transferred) throws IOException {
//                statusBarProgress.setText("test");
//                float percent = (float) (transferred * 100) / size;
//                statusBarProgress.setFraction(percent);
//                consoleView.print(String.format("%s%%", percent), ConsoleViewContentType.SYSTEM_OUTPUT);
            }
        };
    }
}

