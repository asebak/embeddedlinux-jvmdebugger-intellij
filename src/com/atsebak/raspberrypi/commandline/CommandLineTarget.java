package com.atsebak.raspberrypi.commandline;

import com.atsebak.raspberrypi.runner.conf.RaspberryPIRunConfiguration;
import com.intellij.execution.configurations.JavaParameters;
import lombok.Builder;

import java.util.Map;

@Builder
public class CommandLineTarget {
    private final JavaParameters parameters;
    private final RaspberryPIRunConfiguration raspberryPIRunConfiguration;
    private final boolean isDebugging;

    /**
     * Builds the command line command to invoke the java from the target machine
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder cmdBuf = new StringBuilder();
        addRunAsRootOption(cmdBuf);
        addEnvironmentVariables(cmdBuf);
        cmdBuf.append(" java ");
        addDebugOptions(cmdBuf);
        addVMArguments(cmdBuf);
        addClasspath(cmdBuf);
        addMainType(cmdBuf);
        addArguments(cmdBuf);
        return cmdBuf.toString().replaceAll("\\s{2,}", " ").trim();
    }

    private void addArguments(StringBuilder cmdBuf) {
        //todo add java arguments
    }

    /**
     * Adds Main class
     *
     * @param cmdBuf
     */
    private void addMainType(StringBuilder cmdBuf) {
        cmdBuf.append(" ").append(parameters.getMainClass()).append(" ");
    }

    /**
     * Adds the classpath to java app
     *
     * @param cmdBuf
     */
    private void addClasspath(StringBuilder cmdBuf) {
        cmdBuf.append(" -cp classes:lib/'*' ");
    }

    /**
     * Adds Virtual Machine Arguments
     * @param cmdBuf
     */
    private void addVMArguments(StringBuilder cmdBuf) {
        if (isDebugging) {
            //debugging with the port this is added on the remote device command line
            cmdBuf.append("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" +
                    raspberryPIRunConfiguration.getRunnerParameters().getPort());
        }
    }

    /**
     * Adds debug options
     * @param cmdBuf
     */
    private void addDebugOptions(StringBuilder cmdBuf) {
        if (!parameters.getProgramParametersList().getParameters().isEmpty()) {
            for (String arg : parameters.getProgramParametersList().getParameters()) {
                cmdBuf.append(' ').append(arg.trim());
            }
        }
    }

    /**
     * Adds Environment Variables
     *
     * @param cmdBuf
     */
    private void addEnvironmentVariables(StringBuilder cmdBuf) {
        cmdBuf.append(" ");
        for (Map.Entry<String, String> entry : parameters.getEnv().entrySet()) {
            String value = entry.getValue().replaceAll("\"", "\\\"");
            cmdBuf.append(entry.getKey()).append("=\"").append(value).append("\" ");
        }
        cmdBuf.append(" ");
    }

    /**
     * Adds the sudo user to command
     * @param cmdBuf
     */
    private void addRunAsRootOption(StringBuilder cmdBuf) {
        if (raspberryPIRunConfiguration.getRunnerParameters().isRunAsRoot()) {
            cmdBuf.append(" sudo ");
        }
    }
}
