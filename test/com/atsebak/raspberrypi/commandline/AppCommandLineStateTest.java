package com.atsebak.raspberrypi.commandline;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
public class AppCommandLineStateTest {

    @Test
    public void testDebugPortInName() {
        AppCommandLineState commandLineState = Whitebox.newInstance(AppCommandLineState.class);
        assert (AppCommandLineState.getRunConfigurationName("100").contains("100"));
    }

}