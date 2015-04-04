package com.atsebak.raspberrypi.commandline;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class LinuxCommandTest {

    @Test
    public void testToString() throws Exception {
        String s = LinuxCommand.builder().commands(Arrays.asList("rm -r sample.file", "cd home")).build().toString();
        assertEquals("rm -r sample.file; cd home;", s);
    }
}