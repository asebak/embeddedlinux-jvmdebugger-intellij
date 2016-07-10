package com.atsebak.embeddedlinuxjvm.commandline;

import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CommandLineTargetTest {
    JavaParameters javaParameters = Mockito.mock(JavaParameters.class);
    EmbeddedLinuxJVMRunConfiguration piRunConfiguration = Mockito.mock(EmbeddedLinuxJVMRunConfiguration.class);
    EmbeddedLinuxJVMRunConfigurationRunnerParameters parameters = new EmbeddedLinuxJVMRunConfigurationRunnerParameters();
    ParametersList parametersList = Mockito.mock(ParametersList.class);
    ParametersList jvmParametersList = Mockito.mock(ParametersList.class);

    @Before
    public void setUp() {
        parameters.setRunAsRoot(true);
        parameters.setMainclass("com.test.Main");
        parameters.setPort("4000");
        parameters.setHostname("127.0.0.1");
        when(piRunConfiguration.getRunnerParameters()).thenReturn(parameters);
        when(javaParameters.getProgramParametersList()).thenReturn(parametersList);
        when(javaParameters.getProgramParametersList().getParameters()).thenReturn(new ArrayList<String>());
        when(javaParameters.getVMParametersList()).thenReturn(jvmParametersList);
        when(javaParameters.getMainClass()).thenReturn(parameters.getMainclass());
    }

    @Test
    public void testRunCommand() {
        when(javaParameters.getVMParametersList().getParameters()).thenReturn(new ArrayList<String>());
        String runCommand = CommandLineTarget.builder()
                .isDebugging(false)
                .parameters(javaParameters)
                .embeddedLinuxJVMRunConfiguration(piRunConfiguration)
                .build().toString();
        assertTrue(runCommand.contains(String.format("sudo java -cp classes:lib/'*' %s", parameters.getMainclass())));
    }

    @Test
    public void testDebugCommand() {
        JavaParameters params = Mockito.mock(JavaParameters.class);
        String debugCommand = CommandLineTarget.builder()
                .isDebugging(true)
                .parameters(javaParameters)
                .embeddedLinuxJVMRunConfiguration(piRunConfiguration)
                .build().toString();
        assertTrue(debugCommand.contains("sudo java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + parameters.getPort()));
    }

    @Test
    public void testAddingArguments() {
        when(javaParameters.getProgramParametersList().getParameters()).thenReturn(Arrays.asList("1", "2", "3"));
        String runCommand = CommandLineTarget.builder()
                .isDebugging(false)
                .parameters(javaParameters)
                .embeddedLinuxJVMRunConfiguration(piRunConfiguration)
                .build().toString();
        assertTrue(runCommand.contains(String.format("sudo java -cp classes:lib/'*' %s %s", parameters.getMainclass(), "1 2 3")));
    }

    @Test
    public void testRemoveJavaAgent() {
        when(jvmParametersList.getParameters()).thenReturn(Arrays.asList("-javaagent:123", "-foo:bar"));
        String runCommand = CommandLineTarget.builder()
                .isDebugging(false)
                .parameters(javaParameters)
                .embeddedLinuxJVMRunConfiguration(piRunConfiguration)
                .build().toString();
        System.out.println(runCommand);
        assertTrue(runCommand.contains(String.format("sudo java %s -cp classes:lib/'*' %s", "-foo:bar" ,parameters.getMainclass())));
    }

}