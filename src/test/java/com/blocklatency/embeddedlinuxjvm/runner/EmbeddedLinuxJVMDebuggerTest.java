package com.blocklatency.embeddedlinuxjvm.runner;

import com.blocklatency.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class EmbeddedLinuxJVMDebuggerTest {
    @Test
    public void testGetRunnerId() {
        EmbeddedLinuxJVMDebugger debugger = Whitebox.newInstance(EmbeddedLinuxJVMDebugger.class);
        String runnerId = debugger.getRunnerId();
        assertEquals("RaspberryPIDebugger", runnerId);
    }


    @Test
    public void testCanRun() {
        RunProfile profile = Mockito.mock(EmbeddedLinuxJVMRunConfiguration.class);

        EmbeddedLinuxJVMDebugger debugger = Whitebox.newInstance(EmbeddedLinuxJVMDebugger.class);
        boolean canRun = debugger.canRun("Debug", profile);
        assertTrue(canRun);

        RunProfile wrongProfile = Mockito.mock(RunProfile.class);
        boolean cannotRun = debugger.canRun("Debug", wrongProfile);
        assertFalse(cannotRun);
    }
}
