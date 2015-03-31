package com.atsebak.raspberrypi.console;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import lombok.Builder;

import java.io.IOException;
import java.io.OutputStream;

@Builder
public class PIErrorOutputStream extends OutputStream {
    private Project project;

    /**
     * How To write the output stream
     *
     * @param b
     * @throws IOException
     */
    @Override
    public void write(int b) throws IOException {
        PIConsoleView.getInstance(project).print(String.valueOf((char) b), ConsoleViewContentType.ERROR_OUTPUT);
    }
}