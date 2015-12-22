package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.jcraft.jsch.SftpProgressMonitor;

public class SFTPProgress implements SftpProgressMonitor {
    private double count;
    private double max;
    private String src;
    private int percent;
    private int lastDisplayedPercent;

    SFTPProgress() {
        count = 0;
        max = 0;
        percent = 0;
        lastDisplayedPercent = 0;
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        this.max = max;
        this.src = src;
        count = 0;
        percent = 0;
        lastDisplayedPercent = 0;
        status();
    }

    @Override
    public boolean count(long count) {
        this.count += count;
        percent = (int) ((this.count / max) * 100.0);
        status();
        return true;
    }

    @Override
    public void end() {
        percent = (int) ((count / max) * 100.0);
        status();
    }

    private void status() {
        if (lastDisplayedPercent <= percent - 10) {
            System.out.println(src + ": " + percent + "% " + ((long) count) + "/" + ((long) max));
            lastDisplayedPercent = percent;
        }
    }
}
