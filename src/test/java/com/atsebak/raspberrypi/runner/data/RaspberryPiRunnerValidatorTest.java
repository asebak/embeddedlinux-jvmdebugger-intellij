package com.atsebak.raspberrypi.runner.data;

import org.junit.Test;

import static org.junit.Assert.fail;

public class RaspberryPiRunnerValidatorTest {
    RaspberryPIRunnerParameters piRunnerParameters = new RaspberryPIRunnerParameters();

    @Test
    public void testCheckPiSettings() {
        piRunnerParameters.setHostname("10.42.0.25");
        piRunnerParameters.setPort("40");
        piRunnerParameters.setMainclass("Main");
        piRunnerParameters.setUsername("testuser");
        piRunnerParameters.setPassword("testpasssword");
        try {
            RaspberryPiRunnerValidator.checkPiSettings(piRunnerParameters);
        } catch (Exception e) {
            fail("Should not have thrown any RuntimeConfigurationException");
        }
    }
}