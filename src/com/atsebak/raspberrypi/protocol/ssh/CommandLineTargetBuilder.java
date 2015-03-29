package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.runner.RaspberryPIRunConfiguration;
import com.atsebak.raspberrypi.runner.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.JavaParameters;

import java.util.Map;


public class CommandLineTargetBuilder {
    private final RaspberryPIRunConfiguration configuration;
    private final JavaParameters parameters;
    private final RaspberryPIRunnerParameters runnerParameters;

    /**
     * @param configuration PI Configuration
     * @param parameters    Java Parameters
     */
    public CommandLineTargetBuilder(final RaspberryPIRunConfiguration configuration, final JavaParameters parameters) {
        this.configuration = configuration;
        this.runnerParameters = configuration.getRunnerParameters();
        this.parameters = parameters;
    }

    /**
     * Builds the command line command to invoke the java from the target machine
     *
     * @return
     */
    public String buildCommandLine() {
        StringBuilder cmdBuf = new StringBuilder();
        addRunAsRootOption(cmdBuf);
        addEnvironmentVariables(cmdBuf);
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
        //todo add java arguments

    }

    private void addMainType(StringBuilder cmdBuf) {
        cmdBuf.append(" ").append(parameters.getMainClass()).append(" ");
    }

    /**
     * Adds the classpath to java app
     *
     * @param cmdBuf
     */
    private void addClasspath(StringBuilder cmdBuf) {
        cmdBuf.append(" -cp . ");
    }

    /**
     * Adds Virtual Machine Arguments
     * @param cmdBuf
     */
    private void addVMArguments(StringBuilder cmdBuf) {
        cmdBuf.append("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + runnerParameters.getPort());
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
        if (runnerParameters.isRunAsRoot()) {
            cmdBuf.append(" sudo ");
        }
    }
}
