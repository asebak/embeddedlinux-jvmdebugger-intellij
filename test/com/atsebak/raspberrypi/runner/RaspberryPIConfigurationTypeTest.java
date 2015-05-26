package com.atsebak.raspberrypi.runner;

import com.atsebak.embeddedlinuxjvm.runner.conf.RaspberryPIConfigurationType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RaspberryPIConfigurationTypeTest {
    private RaspberryPIConfigurationType configurationType = new RaspberryPIConfigurationType();

    @Test
    public void testSettings() throws Exception {
        String displayName = configurationType.getDisplayName();
        String description = configurationType.getConfigurationTypeDescription();
        assert (displayName == "Raspberry PI");
        assert (description != null && description != "");
    }

}
