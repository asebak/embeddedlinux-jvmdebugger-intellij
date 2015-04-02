package com.atsebak.raspberrypi.runner.data;

import com.intellij.execution.configurations.RuntimeConfigurationWarning;
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
            fail("Should not have thrown any RuntimeConfigurationWarning");
        }
    }

    @Test(expected = RuntimeConfigurationWarning.class)
    public void nullRunnerSettings() throws RuntimeConfigurationWarning {
        RaspberryPiRunnerValidator.checkPiSettings(piRunnerParameters);
    }
}