package com.atsebak.raspberrypi.console;

import com.google.common.io.Closeables;
import com.intellij.execution.ui.ConsoleViewContentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT;
import static com.intellij.execution.ui.ConsoleViewContentType.NORMAL_OUTPUT;


public class PIOutputForwarder {
    private static final int SIZE = 2048;

    @NotNull
    private final ByteArrayOutputStream myStdErr;
    @NotNull
    private final ByteArrayOutputStream myOutput;
    @NotNull
    private final PIConsoleView myConsoleView;

    private ConsoleViewContentType myPreviousContentType;

    public PIOutputForwarder(@NotNull PIConsoleView consoleView) {
        myConsoleView = consoleView;
        myStdErr = new ByteArrayOutputStream(SIZE);
        myOutput = new ByteArrayOutputStream(SIZE * 2);
    }

    public void attachTo(@Nullable Listener listener) {
        OutputStream stdout = new ConsoleAwareOutputStream(this, NORMAL_OUTPUT, listener);
        OutputStream stderr = new ConsoleAwareOutputStream(this, ERROR_OUTPUT, listener);

        System.setOut(new PrintStream(stdout));
        System.setErr(new PrintStream(stderr));
    }

    void close() {
        try {
            Closeables.close(myOutput, true /* swallowIOException */);
            Closeables.close(myStdErr, true /* swallowIOException */);
        } catch (IOException e) {
            // Cannot happen
        }
    }

    @NotNull
    String getStdErr() {
        return myStdErr.toString();
    }

    void write(@NotNull ConsoleViewContentType contentType, @NotNull byte[] b, int off, int len) {
        boolean addNewLine = false;
        // We are combining input from stdout and stderr and that we want to make sure that whenever the output is mixed it starts on a new
        // line.
        if (contentType != myPreviousContentType) {
            addNewLine = myPreviousContentType != null;
            myPreviousContentType = contentType;
        }
        String lineSeparator = System.getProperty("line.separator");
        boolean newLineAdded = false;
        if (addNewLine) {
            byte[] bytes = lineSeparator.getBytes();
            myOutput.write(bytes, 0, bytes.length);
            myConsoleView.print(lineSeparator, contentType);
            newLineAdded = true;
        }
        String text = new String(b, off, len);
        if (lineSeparator.equals(text) && newLineAdded) {
            return;
        }
        myOutput.write(b, off, len);
        if (contentType == ERROR_OUTPUT) {
            myStdErr.write(b, off, len);
        }
        myConsoleView.print(text, contentType);
    }

    @Override
    public String toString() {
        return myOutput.toString();
    }

    interface Listener {
        void onOutput(@NotNull ConsoleViewContentType contentType, @NotNull byte[] data, int offset, int length);
    }

    private static class ConsoleAwareOutputStream extends OutputStream {
        @NotNull
        private final PIOutputForwarder myOutput;
        @NotNull
        private final ConsoleViewContentType myContentType;
        @Nullable
        private final Listener myListener;

        ConsoleAwareOutputStream(@NotNull PIOutputForwarder output,
                                 @NotNull ConsoleViewContentType contentType,
                                 @Nullable Listener listener) {
            myOutput = output;
            myContentType = contentType;
            myListener = listener;
        }

        @Override
        public void write(int b) {
        }

        @Override
        public void write(@NotNull byte[] b, int off, int len) {
            if (myListener != null) {
                myListener.onOutput(myContentType, b, off, len);
            }
            myOutput.write(myContentType, b, off, len);
        }
    }
}
