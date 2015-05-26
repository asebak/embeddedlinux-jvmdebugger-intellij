package com.atsebak.embeddedlinuxjvm.runner.data;

import com.intellij.execution.configurations.RuntimeConfigurationWarning;
import org.junit.Test;

import static org.junit.Assert.fail;

public class EmbeddedLinuxJVMRunnerValidatorTest {
    EmbeddedLinuxJVMRunConfigurationRunnerParameters piRunnerParameters = new EmbeddedLinuxJVMRunConfigurationRunnerParameters();

    @Test
    public void testCheckPiSettings() {
        piRunnerParameters.setHostname("10.42.0.25");
        piRunnerParameters.setPort("40");
        piRunnerParameters.setMainclass("Main");
        piRunnerParameters.setUsername("testuser");
        piRunnerParameters.setPassword("testpasssword");
        try {
            EmbeddedLinuxJVMRunnerValidator.checkPiSettings(piRunnerParameters);
        } catch (Exception e) {
            fail("Should not have thrown any RuntimeConfigurationWarning");
        }
    }

    @Test(expected = RuntimeConfigurationWarning.class)
    public void nullRunnerSettings() throws RuntimeConfigurationWarning {
        EmbeddedLinuxJVMRunnerValidator.checkPiSettings(piRunnerParameters);
    }
}