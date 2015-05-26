package com.atsebak.raspberrypi.console;


import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMToolWindowFactory;
import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.intellij.openapi.components.ServiceManager;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({EmbeddedLinuxJVMConsoleView.class, ServiceManager.class})
public class EmbeddedLinuxJVMToolWindowFactoryTest {

    @Test
    public void testConsoleID() {
        assert (EmbeddedLinuxJVMToolWindowFactory.ID.contains("Console"));
    }
}