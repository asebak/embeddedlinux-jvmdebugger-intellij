package com.atsebak.raspberrypi.commandline;


import com.atsebak.raspberrypi.runner.conf.RaspberryPIRunConfiguration;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class AppCommandLineStateTest {

    ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
    RaspberryPIRunConfiguration runConfiguration = Mockito.mock(RaspberryPIRunConfiguration.class);
    RunnerSettings runnerSettings = Mockito.mock(RunnerSettings.class);

    @Test
    public void testRunningDebugMode() {
        when(executionEnvironment.getRunnerSettings()).thenReturn(runnerSettings);
        AppCommandLineState commandLineState = new AppCommandLineState(executionEnvironment, runConfiguration);
    }
}