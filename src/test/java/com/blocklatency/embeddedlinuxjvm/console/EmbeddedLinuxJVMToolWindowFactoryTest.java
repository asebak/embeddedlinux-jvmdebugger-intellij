package com.blocklatency.embeddedlinuxjvm.console;


import com.intellij.openapi.components.ServiceManager;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertTrue;


@PrepareForTest({EmbeddedLinuxJVMConsoleView.class, ServiceManager.class})
public class EmbeddedLinuxJVMToolWindowFactoryTest {

    @Test
    public void testConsoleID() {
        assertTrue(EmbeddedLinuxJVMToolWindowFactory.ID.contains("Console"));
    }
}