package com.blocklatency.embeddedlinuxjvm.ui;


import com.blocklatency.embeddedlinuxjvm.project.RPiJavaModuleBuilder;
import com.intellij.openapi.options.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class EmbeddedJavaModuleStepTest {

    com.blocklatency.embeddedlinuxjvm.project.RPiJavaModuleBuilder RPiJavaModuleBuilder = Mockito.mock(RPiJavaModuleBuilder.class);
    private EmbeddedJavaModuleStep embeddedJavaModuleStep;

    @Before
    public void setUp() {
        embeddedJavaModuleStep = new EmbeddedJavaModuleStep(RPiJavaModuleBuilder);
    }

    @Test
    public void validPackageName() throws ConfigurationException {
        embeddedJavaModuleStep.setPackageName("com.blocklatency");
        boolean validate = embeddedJavaModuleStep.validate();
        assertEquals(validate, true);
    }

    @Test(expected = ConfigurationException.class)
    public void badPackageName() throws ConfigurationException {
        embeddedJavaModuleStep.setPackageName("com.blocklatency--");
        boolean validate = embeddedJavaModuleStep.validate();
        assertEquals(validate, false);
    }
}