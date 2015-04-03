package com.atsebak.raspberrypi.commandline;

import lombok.Builder;

import java.util.Collection;

@Builder
public class LinuxCommandBuilder {
    private Collection<String> commands;

    @Override
    public String toString() {
        return null;
    }
}
