package com.atsebak.raspberrypi.commandline;

import lombok.Builder;

import java.util.Collection;

@Builder
public class LinuxCommand {
    private Collection<String> commands;

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String command : commands) {
            stringBuilder.append(command).append(";").append(" ");
        }
        return stringBuilder.toString().replaceAll("\\s{2,}", " ").trim();
    }
}
