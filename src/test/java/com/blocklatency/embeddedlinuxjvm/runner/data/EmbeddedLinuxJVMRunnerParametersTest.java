package com.blocklatency.embeddedlinuxjvm.runner.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class EmbeddedLinuxJVMRunnerParametersTest {

    @Test
    public void testCloneMechanism() {
        EmbeddedLinuxJVMRunConfigurationRunnerParameters embeddedLinuxJVMRunConfigurationRunnerParameters = new EmbeddedLinuxJVMRunConfigurationRunnerParameters();
        embeddedLinuxJVMRunConfigurationRunnerParameters.setClassesDirectory("/main/target");
        embeddedLinuxJVMRunConfigurationRunnerParameters.setHostname("10.42.0.224");
        embeddedLinuxJVMRunConfigurationRunnerParameters.setModuleName("mymodule");
        embeddedLinuxJVMRunConfigurationRunnerParameters.setMainclass("com.raspberrypi.Main");
        embeddedLinuxJVMRunConfigurationRunnerParameters.setPort("100");
        embeddedLinuxJVMRunConfigurationRunnerParameters.setPassword("tester");
        EmbeddedLinuxJVMRunConfigurationRunnerParameters clone = embeddedLinuxJVMRunConfigurationRunnerParameters.clone();
        assertEquals(embeddedLinuxJVMRunConfigurationRunnerParameters, clone);
    }
}