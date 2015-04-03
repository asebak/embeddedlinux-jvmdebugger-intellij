package com.atsebak.raspberrypi.ui;


import com.atsebak.raspberrypi.project.PIJavaModuleBuilder;
import com.intellij.openapi.options.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class PIJavaModuleStepTest {

    PIJavaModuleBuilder piJavaModuleBuilder = Mockito.mock(PIJavaModuleBuilder.class);
    private PIJavaModuleStep piJavaModuleStep;

    @Before
    public void setUp() {
        piJavaModuleStep = new PIJavaModuleStep(piJavaModuleBuilder);
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