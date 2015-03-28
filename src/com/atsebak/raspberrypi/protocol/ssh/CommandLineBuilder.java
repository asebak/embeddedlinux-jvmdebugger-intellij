package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.runner.RaspberryPIRunConfiguration;
import com.atsebak.raspberrypi.runner.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.JavaParameters;

import java.util.Map;

/**
 * Created by asebak on 28/03/15.
 */
public class CommandLineBuilder {
    private final RaspberryPIRunConfiguration configuration;
    private final JavaParameters parameters;
    private final RaspberryPIRunnerParameters runnerParameters;

    public CommandLineBuilder(final RaspberryPIRunConfiguration configuration, final JavaParameters parameters) {
        this.configuration = configuration;
        this.runnerParameters = configuration.getRunnerParameters();
        this.parameters = parameters;
        String jarPath = parameters.getJarPath();
    }

    public String buildCommandLine() {
        StringBuilder cmdBuf = new StringBuilder();
        addRunAsRootOption(cmdBuf);
        addEnvironmentVariables(cmdBuf, "");
        cmdBuf.append(" java ");
        addDebugOptions(cmdBuf);
        addVMArguments(cmdBuf);
        addClasspath(cmdBuf);
        addMainType(cmdBuf);
        addArguments(cmdBuf);
        cmdBuf.append(" ;");
        return cmdBuf.toString();
    }

    private void addArguments(StringBuilder cmdBuf) {
//        cmdBuf.append(' ').append(parameters.ge);
    }

    private void addMainType(StringBuilder cmdBuf) {
        cmdBuf.append(" ").append(parameters.getMainClass()).append(" ");
    }

    private void addClasspath(StringBuilder cmdBuf) {
        cmdBuf.append(" -cp . ");
//        cmdBuf.append(" -cp classes:lib/'*' ");
    }

    private void addVMArguments(StringBuilder cmdBuf) {
        if (!parameters.getVMParametersList().getParameters().isEmpty()) {
            for (String arg : parameters.getVMParametersList().getParameters()) {
                cmdBuf.append(' ').append(arg.trim());
            }
        }
    }

    private void addDebugOptions(StringBuilder cmdBuf) {
        if (!parameters.getProgramParametersList().getParameters().isEmpty()) {
            for (String arg : parameters.getProgramParametersList().getParameters()) {
                cmdBuf.append(' ').append(arg.trim());
            }
        }
    }

    private void addEnvironmentVariables(StringBuilder cmdBuf, String homeFolder) {
        cmdBuf.append(" ");
        for (Map.Entry<String, String> entry : parameters.getEnv().entrySet()) {
            String value = entry.getValue().replaceAll("\"", "\\\"");
            cmdBuf.append(entry.getKey()).append("=\"").append(value).append("\" ");
        }
        cmdBuf.append(" ");
    }

    private void addRunAsRootOption(StringBuilder cmdBuf) {
        if (runnerParameters.isRunAsRoot()) {
            cmdBuf.append(" sudo ");
        }
    }
}
