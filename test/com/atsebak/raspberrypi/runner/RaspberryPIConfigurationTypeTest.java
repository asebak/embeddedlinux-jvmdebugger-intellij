package com.atsebak.raspberrypi.runner;

import org.junit.Test;

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