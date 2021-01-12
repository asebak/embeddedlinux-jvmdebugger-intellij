package com.atsebak.embeddedlinuxjvm.runner;

import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class EmbeddedLinuxJVMRunnerTest {
    @Test
    public void testGetRunnerId() {
        EmbeddedLinuxJVMRunner debugger = Whitebox.newInstance(EmbeddedLinuxJVMRunner.class);
        String runnerId = debugger.getRunnerId();
        assertEquals("RaspberryPIRunner", runnerId);
    }

    @Test
    public void testCanRun() {
        RunProfile profile = Mockito.mock(EmbeddedLinuxJVMRunConfiguration.class);

        EmbeddedLinuxJVMRunner runner = Whitebox.newInstance(EmbeddedLinuxJVMRunner.class);
        boolean canRun = runner.canRun("Run", profile);
        assertTrue(canRun);

        boolean cannotRun = runner.canRun("Debug", profile);
        assertFalse(cannotRun);

        RunProfile wrongProfile = Mockito.mock(RunProfile.class);
        boolean cannotRun2 = runner.canRun("Run", wrongProfile);
        assertFalse(cannotRun2);
    }
}
