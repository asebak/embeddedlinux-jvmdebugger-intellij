package com.atsebak.embeddedlinuxjvm.utils;


import com.atsebak.embeddedlinuxjvm.hal.HostMachineHAL;
import org.jetbrains.annotations.NotNull;

public class RemoteJdk {

    /**
     * Sets command line
     * @param hostMachineHAL
     * @return
     */
    public static RemoteCommandLine setupJVMCommandLine(@NotNull HostMachineHAL hostMachineHAL) {
        final RemoteCommandLine commandLine = new RemoteCommandLine();
        commandLine.setExePath(hostMachineHAL.getCommandLineName());
        return commandLine;
    }

}
