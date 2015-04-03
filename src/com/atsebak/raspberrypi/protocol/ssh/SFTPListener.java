package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.console.PIConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.xfer.TransferListener;

import java.io.IOException;

public class SFTPListener implements TransferListener {
    private final PIConsoleView consoleView;
    private final String relPath;


    public SFTPListener(String relPath, PIConsoleView consoleView) {
        this.consoleView = consoleView;
        this.relPath = relPath;
    }

    public TransferListener directory(String name) {
        return new SFTPListener(this.relPath + name + "/", consoleView);
    }

    public StreamCopier.Listener file(String name, final long size) {
        return new StreamCopier.Listener() {
            public void reportProgress(long transferred) throws IOException {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                consoleView.print(String.format("Transferred %s of %s \n\r", transferred, size), ConsoleViewContentType.SYSTEM_OUTPUT);
            }
        };
    }
}

