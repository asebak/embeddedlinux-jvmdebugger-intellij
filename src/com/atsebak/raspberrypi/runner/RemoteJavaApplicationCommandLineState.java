package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.protocol.ssh.CommandLineTargetBuilder;
import com.atsebak.raspberrypi.protocol.ssh.SSHUploader;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.JavaRunConfigurationExtensionManager;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.util.PathsList;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class RemoteJavaApplicationCommandLineState extends JavaCommandLineState {
    private final RaspberryPIRunConfiguration configuration;

    public RemoteJavaApplicationCommandLineState(@NotNull final RaspberryPIRunConfiguration configuration,
                                                 final ExecutionEnvironment environment) {
        super(environment);
        this.configuration = configuration;
    }

    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        OSProcessHandler handler = super.startProcess();
        handler.setShouldDestroyProcessRecursively(true);
        //todo fix console view maybe here?
        final RunnerSettings runnerSettings = getRunnerSettings();
        JavaRunConfigurationExtensionManager.getInstance().attachExtensionsToProcess(configuration, handler, runnerSettings);
        return handler;
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        final JavaParameters params = new JavaParameters();
        final JavaRunConfigurationModule module = configuration.getConfigurationModule();
        final int classPathType = JavaParametersUtil.getClasspathType(module,
                configuration.MAIN_CLASS_NAME,
                false);
        final String jreHome = configuration.ALTERNATIVE_JRE_PATH_ENABLED ? configuration.ALTERNATIVE_JRE_PATH : null;
        JavaParametersUtil.configureModule(module, params, classPathType, jreHome);
        params.setMainClass(configuration.MAIN_CLASS_NAME);
        PathsList classPath = params.getClassPath();

        CommandLineTargetBuilder cmdBuilder = new CommandLineTargetBuilder(configuration, params);
        invokeSSH(classPath.getPathList().get(classPath.getPathList().size() - 1), cmdBuilder);
        params.getVMParametersList().addParametersString("-Xdebug -Xrunjdwp:transport=dt_socket,server=n,suspend=n,address=" +
                configuration.getRunnerParameters().getHostname() + ":" + configuration.getRunnerParameters().getPort());


        return params;
    }

    private void invokeSSH(String projectOutput, CommandLineTargetBuilder builder) {
        RaspberryPIRunnerParameters runnerParameters = configuration.getRunnerParameters();
        SSHUploader uploader = new SSHUploader();
        try {
            uploader.uploadToTarget(runnerParameters, new File(projectOutput), builder.buildCommandLine());
        } catch (Exception e) {
        }
    }

}