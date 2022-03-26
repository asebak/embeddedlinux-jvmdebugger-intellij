package com.blocklatency.embeddedlinuxjvm.runner;

import com.blocklatency.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMConfigurationType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class EmbeddedLinuxJVMConfigurationTypeTest {
    private EmbeddedLinuxJVMConfigurationType configurationType = new EmbeddedLinuxJVMConfigurationType();

    @Test
    public void testSettings() throws Exception {
        String displayName = configurationType.getDisplayName();
        String description = configurationType.getConfigurationTypeDescription();
        assertEquals(displayName, "Embedded Linux JVM");
        assertTrue(description != null && !description.equals(""));
    }

}
