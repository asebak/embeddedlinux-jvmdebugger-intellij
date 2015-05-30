package com.atsebak.embeddedlinuxjvm.utils;

import com.atsebak.embeddedlinuxjvm.hal.LinuxHAL;
import com.atsebak.embeddedlinuxjvm.hal.WindowsHAL;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoteJdkTest {

    @Test
    public void testGettingCommandLineFromLinux() {
        LinuxHAL linuxHAL = new LinuxHAL();
        RemoteCommandLine remoteCommandLine = RemoteJdk.setupJVMCommandLine(linuxHAL);
        String exePath = remoteCommandLine.getExePath();
        assertEquals(exePath, "bash");
    }

    @Test
    public void testGettingCommandLineFromWindows() {
        WindowsHAL windowsHAL = new WindowsHAL();
        RemoteCommandLine remoteCommandLine = RemoteJdk.setupJVMCommandLine(windowsHAL);
        String exePath = remoteCommandLine.getExePath();
        assertEquals(exePath, "cmd");
    }
}