package com.atsebak.embeddedlinuxjvm.utils;

import com.intellij.execution.configurations.GeneralCommandLine;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;


public class RemoteCommandLine extends GeneralCommandLine {
    @NotNull
    @Override
    protected Process startProcess(List<String> commands) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("bash");
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }
}
