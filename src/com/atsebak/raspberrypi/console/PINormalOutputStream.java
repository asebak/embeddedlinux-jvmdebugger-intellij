package com.atsebak.raspberrypi.console;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by asebak on 29/03/15.
 */
public class PINormalOutputStream extends OutputStream {
    private Project project;

    public PINormalOutputStream(Project project) {
        this.project = project;
    }

    @Override
    public void write(int b) throws IOException {
        PIConsoleView.getInstance(project).print(String.valueOf((char) b), ConsoleViewContentType.NORMAL_OUTPUT);
    }
}