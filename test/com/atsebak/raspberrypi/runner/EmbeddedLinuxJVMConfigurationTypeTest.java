package com.atsebak.raspberrypi.runner;

import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMConfigurationType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class EmbeddedLinuxJVMConfigurationTypeTest {
    private EmbeddedLinuxJVMConfigurationType configurationType = new EmbeddedLinuxJVMConfigurationType();

    @Test
    public void testSettings() throws Exception {
        String displayName = configurationType.getDisplayName();
        String description = configurationType.getConfigurationTypeDescription();
        assert (displayName == "Embedded Linux JVM");
        assert (description != null && description != "");
    }

}
