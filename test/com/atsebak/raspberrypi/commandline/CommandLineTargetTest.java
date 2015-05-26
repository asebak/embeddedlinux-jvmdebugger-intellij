package com.atsebak.raspberrypi.commandline;

import com.atsebak.embeddedlinuxjvm.commandline.CommandLineTarget;
import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

public class CommandLineTargetTest {
    JavaParameters javaParameters = Mockito.mock(JavaParameters.class);
    EmbeddedLinuxJVMRunConfiguration piRunConfiguration = Mockito.mock(EmbeddedLinuxJVMRunConfiguration.class);
    EmbeddedLinuxJVMRunConfigurationRunnerParameters parameters = new EmbeddedLinuxJVMRunConfigurationRunnerParameters();
    ParametersList parametersList = Mockito.mock(ParametersList.class);

    @Before
    public void setUp() {
        parameters.setRunAsRoot(true);
        parameters.setMainclass("com.test.Main");
        parameters.setPort("4000");
        parameters.setHostname("127.0.0.1");
        when(piRunConfiguration.getRunnerParameters()).thenReturn(parameters);
        when(javaParameters.getProgramParametersList()).thenReturn(parametersList);
        when(javaParameters.getProgramParametersList().getParameters()).thenReturn(new ArrayList<String>());
        when(javaParameters.getMainClass()).thenReturn(parameters.getMainclass());
    }

    @Test
    public void testRunCommand() {
        String runCommand = CommandLineTarget.builder()
                .isDebugging(false)
                .parameters(javaParameters)
                .embeddedLinuxJVMRunConfiguration(piRunConfiguration)
                .build().toString();
        assert (runCommand.contains(String.format("sudo java -cp classes:lib/'*' %s", parameters.getMainclass())));
    }

    @Test
    public void testDebugCommand() {
        String debugCommand = CommandLineTarget.builder()
                .isDebugging(true)
                .parameters(javaParameters)
                .embeddedLinuxJVMRunConfiguration(piRunConfiguration)
                .build().toString();
        assert (debugCommand.contains("sudo java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + parameters.getPort()));
    }
}