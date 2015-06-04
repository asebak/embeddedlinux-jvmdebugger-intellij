package com.atsebak.embeddedlinuxjvm.commandline;

import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.intellij.execution.configurations.JavaParameters;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

@Builder
public class CommandLineTarget {
    private final JavaParameters parameters;
    private final EmbeddedLinuxJVMRunConfiguration embeddedLinuxJVMRunConfiguration;
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

    /**
     * Adds project arguments to JVM
     *
     * @param cmdBuf
     */
    private void addArguments(StringBuilder cmdBuf) {
        for (String arg : parameters.getProgramParametersList().getParameters()) {
            if (!arg.contains("transport=dt_socket") && StringUtils.isNotBlank(arg)) {
                cmdBuf.append(' ').append(arg.trim());
            }
        }
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
                    embeddedLinuxJVMRunConfiguration.getRunnerParameters().getPort());
        }
        for (String arg : parameters.getVMParametersList().getParameters()) {
            if (!arg.contains("transport=dt_socket") && StringUtils.isNotBlank(arg)) {
                cmdBuf.append(' ').append(arg.trim());
            }
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
        if (embeddedLinuxJVMRunConfiguration.getRunnerParameters().isRunAsRoot()) {
            cmdBuf.append(" sudo ");
        }
    }
}
