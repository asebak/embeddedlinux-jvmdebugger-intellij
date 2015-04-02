package com.atsebak.raspberrypi.console;

import com.intellij.execution.ui.ConsoleViewContentType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;


public class PIOutputForwarderTest {

    final String testString = "a unit test";
    PIConsoleView piConsoleView = Mockito.mock(PIConsoleView.class);
    PIOutputForwarder outputForwarder;

    @Before
    public void setUp() {
        outputForwarder = new PIOutputForwarder(piConsoleView);
        outputForwarder.attachTo(null);
    }

    @Test
    public void testStringTruncatedOutputLength() throws Exception {
        final String truncatedString = testString.substring(0, Math.min(testString.length(), testString.length() - 1));
        outputForwarder.write(ConsoleViewContentType.NORMAL_OUTPUT, testString.getBytes(), 0, testString.length() - 1);
        Mockito.verify(piConsoleView).print(eq(truncatedString), eq(ConsoleViewContentType.NORMAL_OUTPUT));
    }

    @Test
    public void testStringNormalOutputLength() throws Exception {
        outputForwarder.write(ConsoleViewContentType.NORMAL_OUTPUT, testString.getBytes(), 0, testString.length());
        Mockito.verify(piConsoleView).print(eq(testString), eq(ConsoleViewContentType.NORMAL_OUTPUT));
    }

    @Test
    public void testCreateNewLineOnOutputChange() {
        outputForwarder.write(ConsoleViewContentType.NORMAL_OUTPUT, testString.getBytes(), 0, testString.length());
        Mockito.verify(piConsoleView, never()).print(eq(System.getProperty("line.separator")), eq(ConsoleViewContentType.NORMAL_OUTPUT));
        outputForwarder.write(ConsoleViewContentType.ERROR_OUTPUT, testString.getBytes(), 0, testString.length());
        Mockito.verify(piConsoleView).print(eq(System.getProperty("line.separator")), eq(ConsoleViewContentType.ERROR_OUTPUT));
    }

    @Test
    public void testOutputStreams() {
        outputForwarder.write(ConsoleViewContentType.NORMAL_OUTPUT, testString.concat("\n\r").getBytes(), 0, testString.length());
        assertEquals(outputForwarder.toString(), testString);
        outputForwarder.write(ConsoleViewContentType.ERROR_OUTPUT, testString.getBytes(), 0, testString.length());
        assertEquals(outputForwarder.getStdErr(), testString);
    }
}