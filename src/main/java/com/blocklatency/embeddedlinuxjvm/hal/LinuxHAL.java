package com.blocklatency.embeddedlinuxjvm.hal;


public class LinuxHAL implements HostMachineHAL {
    @Override
    public String getCommandLineName() {
        return "bash";
    }
}
