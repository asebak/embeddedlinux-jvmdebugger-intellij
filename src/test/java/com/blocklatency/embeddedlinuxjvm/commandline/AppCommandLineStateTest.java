package com.blocklatency.embeddedlinuxjvm.commandline;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class AppCommandLineStateTest {

    @Test
    public void testDebugPortInName() {
        assertTrue(AppCommandLineState.getRunConfigurationName("100").contains("100"));
    }

}