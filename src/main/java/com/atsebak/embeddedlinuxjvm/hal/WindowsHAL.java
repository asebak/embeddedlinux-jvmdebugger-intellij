package com.atsebak.embeddedlinuxjvm.hal;


public class WindowsHAL implements HostMachineHAL {
    @Override
    public String getCommandLineName() {
        return "cmd";
    }
}
