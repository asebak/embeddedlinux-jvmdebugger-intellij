package com.blocklatency.embeddedlinuxjvm.hal;


public class WindowsHAL implements HostMachineHAL {
    @Override
    public String getCommandLineName() {
        return "cmd";
    }
}
