package com.atsebak.embeddedlinuxjvm.ui;


import com.atsebak.embeddedlinuxjvm.project.RPiJavaModuleBuilder;
import com.intellij.openapi.options.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class EmbeddedJavaModuleStepTest {

    RPiJavaModuleBuilder RPiJavaModuleBuilder = Mockito.mock(RPiJavaModuleBuilder.class);
    private EmbeddedJavaModuleStep embeddedJavaModuleStep;

    @Before
    public void setUp() {
        embeddedJavaModuleStep = new EmbeddedJavaModuleStep(RPiJavaModuleBuilder);
    }

    @Test
    public void validPackageName() throws ConfigurationException {
        embeddedJavaModuleStep.setPackageName("com.atsebak");
        boolean validate = embeddedJavaModuleStep.validate();
        assertEquals(validate, true);
    }

    @Test(expected = ConfigurationException.class)
    public void badPackageName() throws ConfigurationException {
        embeddedJavaModuleStep.setPackageName("com.atsebak--");
        boolean validate = embeddedJavaModuleStep.validate();
        assertEquals(validate, false);
    }
}