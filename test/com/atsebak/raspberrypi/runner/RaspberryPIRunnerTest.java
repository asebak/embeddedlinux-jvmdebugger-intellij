package com.atsebak.raspberrypi.runner;

import com.atsebak.embeddedlinuxjvm.runner.RaspberryPIRunner;
import com.atsebak.embeddedlinuxjvm.runner.conf.RaspberryPIRunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class RaspberryPIRunnerTest {
    @Test
    public void testGetRunnerId() {
        RaspberryPIRunner debugger = Whitebox.newInstance(RaspberryPIRunner.class);
        String runnerId = debugger.getRunnerId();
        assertEquals("RaspberryPIRunner", runnerId);
    }

    @Test
    public void testCanRun() {
        RunProfile profile = Mockito.mock(RaspberryPIRunConfiguration.class);

        RaspberryPIRunner runner = Whitebox.newInstance(RaspberryPIRunner.class);
        boolean canRun = runner.canRun("Run", profile);
        assertTrue(canRun);

        boolean cannotRun = runner.canRun("Debug", profile);
        assertFalse(cannotRun);

        RunProfile wrongProfile = Mockito.mock(RunProfile.class);
        boolean cannotRun2 = runner.canRun("Run", wrongProfile);
        assertFalse(cannotRun2);
    }
}
