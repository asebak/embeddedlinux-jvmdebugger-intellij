package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.runner.conf.RaspberryPIRunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class RaspberryPIDebuggerTest {
    @Test
    public void testGetRunnerId() {
        RaspberryPIDebugger debugger = Whitebox.newInstance(RaspberryPIDebugger.class);
        String runnerId = debugger.getRunnerId();
        assertEquals("RaspberryPIDebugger", runnerId);
    }


    @Test
    public void testCanRun() {
        RunProfile profile = Mockito.mock(RaspberryPIRunConfiguration.class);

        RaspberryPIDebugger debugger = Whitebox.newInstance(RaspberryPIDebugger.class);
        boolean canRun = debugger.canRun("Debug", profile);
        assertTrue(canRun);

        RunProfile wrongProfile = Mockito.mock(RunProfile.class);
        boolean cannotRun = debugger.canRun("Debug", wrongProfile);
        assertFalse(cannotRun);
    }
}
