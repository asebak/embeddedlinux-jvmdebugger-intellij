package com.atsebak.raspberrypi.ui;


import com.atsebak.embeddedlinuxjvm.project.RPiJavaModuleBuilder;
import com.atsebak.embeddedlinuxjvm.ui.PIJavaModuleStep;
import com.intellij.openapi.options.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class PIJavaModuleStepTest {

    RPiJavaModuleBuilder RPiJavaModuleBuilder = Mockito.mock(RPiJavaModuleBuilder.class);
    private PIJavaModuleStep piJavaModuleStep;

    @Before
    public void setUp() {
        piJavaModuleStep = new PIJavaModuleStep(RPiJavaModuleBuilder);
    }

    @Test
    public void validPackageName() throws ConfigurationException {
        piJavaModuleStep.setPackageName("com.atsebak");
        boolean validate = piJavaModuleStep.validate();
        assertEquals(validate, true);
    }

    @Test(expected = ConfigurationException.class)
    public void badPackageName() throws ConfigurationException {
        piJavaModuleStep.setPackageName("com.atsebak--");
        boolean validate = piJavaModuleStep.validate();
        assertEquals(validate, false);
    }
}