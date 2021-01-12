package com.atsebak.embeddedlinuxjvm.commandline;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class LinuxCommandTest {

    @Test
    public void testToString() throws Exception {
        String s = LinuxCommand.builder().commands(Arrays.asList("rm -r sample.file", "cd home")).build().toString();
        assertEquals("rm -r sample.file; cd home;", s);
    }

    @Test
    public void formatSpacingTest() {
        String s = LinuxCommand.builder().commands(Arrays.asList("   rm -r sample.file", "   cd home")).build().toString();
        assertEquals("rm -r sample.file; cd home;", s);
    }
}