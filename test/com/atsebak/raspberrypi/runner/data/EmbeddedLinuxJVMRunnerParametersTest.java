package com.atsebak.raspberrypi.runner.data;

import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class EmbeddedLinuxJVMRunnerParametersTest {

    @Test
    public void testCloneMechanism() {
        EmbeddedLinuxJVMRunConfigurationRunnerParameters embeddedLinuxJVMRunConfigurationRunnerParameters = new EmbeddedLinuxJVMRunConfigurationRunnerParameters();
        embeddedLinuxJVMRunConfigurationRunnerParameters.setClassesDirectory("/main/target");
        embeddedLinuxJVMRunConfigurationRunnerParameters.setHostname("10.42.0.224");
        embeddedLinuxJVMRunConfigurationRunnerParameters.setMainclass("com.raspberrypi.Main");
        embeddedLinuxJVMRunConfigurationRunnerParameters.setPort("100");
        embeddedLinuxJVMRunConfigurationRunnerParameters.setPassword("tester");
        EmbeddedLinuxJVMRunConfigurationRunnerParameters clone = embeddedLinuxJVMRunConfigurationRunnerParameters.clone();
        assertEquals(embeddedLinuxJVMRunConfigurationRunnerParameters, clone);
    }
}