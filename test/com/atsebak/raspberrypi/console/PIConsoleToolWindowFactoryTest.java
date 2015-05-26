package com.atsebak.raspberrypi.console;


import com.atsebak.embeddedlinuxjvm.console.PIConsoleToolWindowFactory;
import com.atsebak.embeddedlinuxjvm.console.PIConsoleView;
import com.intellij.openapi.components.ServiceManager;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({PIConsoleView.class, ServiceManager.class})
public class PIConsoleToolWindowFactoryTest {

    @Test
    public void testConsoleID() {
        assert (PIConsoleToolWindowFactory.ID.contains("Console"));
    }
}