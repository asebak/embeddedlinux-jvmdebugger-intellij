package com.blocklatency.embeddedlinuxjvm.commandline;

import com.blocklatency.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.intellij.execution.configurations.JavaParameters;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

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
        addVMArguments(cmdBuf);
        addClasspath(cmdBuf);
        addMainType(cmdBuf);
        addArguments(cmdBuf);
        return cmdBuf.toString().replaceAll("\\s{2,}", " ").trim();
    }

    /**
     * Adds Main class
     *
     * @param cmdBuf
     */
    private void addMainType(@NotNull StringBuilder cmdBuf) {
        cmdBuf.append(" ").append(parameters.getMainClass()).append(" ");
    }

    /**
     * Adds the classpath to java app
     *
     * @param cmdBuf
     */
    private void addClasspath(@NotNull StringBuilder cmdBuf) {
        cmdBuf.append(" -cp classes:lib/'*' ");
    }

    /**
     * Adds Virtual Machine Arguments
     * @param cmdBuf
     */
    private void addVMArguments(@NotNull StringBuilder cmdBuf) {
        if (isDebugging) {
            //debugging with the port this is added on the remote device command line
            cmdBuf.append("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" +
                    embeddedLinuxJVMRunConfiguration.getRunnerParameters().getHostname() + ":" + embeddedLinuxJVMRunConfiguration.getRunnerParameters().getPort());
        }
        for (String arg : parameters.getVMParametersList().getParameters()) {
            //todo see if devs need the java agent support because for now this is a quick fix that might not be the proper way
            if (!arg.contains("transport=dt_socket") && !arg.contains("javaagent") && StringUtils.isNotBlank(arg)
                    && !parameters.getProgramParametersList().getParameters().equals(parameters.getVMParametersList().getParameters())) {
                cmdBuf.append(' ').append(arg.trim());
            }
        }
    }

    /**
     * Adds debug options
     * @param cmdBuf
     */
    private void addArguments(@NotNull StringBuilder cmdBuf) {
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
    private void addEnvironmentVariables(@NotNull StringBuilder cmdBuf) {
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
    private void addRunAsRootOption(@NotNull StringBuilder cmdBuf) {
        if (embeddedLinuxJVMRunConfiguration.getRunnerParameters().isRunAsRoot()) {
            cmdBuf.append(" sudo ");
        }
    }
}
