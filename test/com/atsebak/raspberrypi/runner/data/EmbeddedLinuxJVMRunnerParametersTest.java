package com.atsebak.raspberrypi.runner.data;

import com.atsebak.embeddedlinuxjvm.runner.data.RaspberryPIRunnerParameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class EmbeddedLinuxJVMRunnerParametersTest {

    @Test
    public void testCloneMechanism() {
        RaspberryPIRunnerParameters raspberryPIRunnerParameters = new RaspberryPIRunnerParameters();
        raspberryPIRunnerParameters.setClassesDirectory("/main/target");
        raspberryPIRunnerParameters.setHostname("10.42.0.224");
        raspberryPIRunnerParameters.setMainclass("com.raspberrypi.Main");
        raspberryPIRunnerParameters.setPort("100");
        raspberryPIRunnerParameters.setPassword("tester");
        RaspberryPIRunnerParameters clone = raspberryPIRunnerParameters.clone();
        assertEquals(raspberryPIRunnerParameters, clone);
    }
}