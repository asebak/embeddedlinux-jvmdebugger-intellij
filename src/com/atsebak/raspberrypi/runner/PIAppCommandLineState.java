package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.console.PIConsoleFilter;
import com.atsebak.raspberrypi.protocol.ssh.CommandLineTargetBuilder;
import com.atsebak.raspberrypi.protocol.ssh.SSHUploader;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.util.PathsList;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PIAppCommandLineState extends JavaCommandLineState {
    private final RaspberryPIRunConfiguration configuration;

    public PIAppCommandLineState(@NotNull final RaspberryPIRunConfiguration configuration,
                                 final ExecutionEnvironment environment) {
        super(environment);
        this.configuration = configuration;
        addConsoleFilters(new PIConsoleFilter(getEnvironment().getProject()));
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        return super.execute(executor, runner);
//        OSProcessHandler handler = this.startProcess();
//        final TextConsoleBuilder textConsoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(getEnvironment().getProject());
//        textConsoleBuilder.setViewer(true);
//        textConsoleBuilder.getConsole().print("ASDSADSADA", ConsoleViewContentType.NORMAL_OUTPUT);
//        textConsoleBuilder.getConsole().attachToProcess(handler);
//        return new DefaultExecutionResult(textConsoleBuilder.getConsole(), handler);
    }

//    @NotNull
//    @Override
//    protected OSProcessHandler startProcess() throws ExecutionException {
//        final OSProcessHandler handler = JavaCommandLineStateUtil.startProcess(createCommandLine());
//        ProcessTerminatedListener.attach(handler, configuration.getProject(), JavadocBundle.message("javadoc.generate.exited"));
//        handler.addProcessListener(new ProcessAdapter() {
//            @Override
//            public void startNotified(ProcessEvent event) {
//                super.startNotified(event);
//            }
//
//            @Override
//            public void onTextAvailable(ProcessEvent event, Key outputType) {
//                super.onTextAvailable(event, outputType);
//            }
//
//            @Override
//            public void processTerminated(ProcessEvent event) {
//                super.processTerminated(event);
//            }
//
//            @Override
//            public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
//                super.processWillTerminate(event, willBeDestroyed);
//            }
//        });
//        return handler;
//    }

    /**
     * Creates the necessary Java paramaters for the application.
     *
     * @return
     * @throws ExecutionException
     */
    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        final JavaParameters params = new JavaParameters();
        final JavaRunConfigurationModule module = configuration.getConfigurationModule();
        final int classPathType = JavaParametersUtil.getClasspathType(module,
                configuration.getRunClass(),
                false);
        final String jreHome = configuration.isAlternativeJrePathEnabled() ? configuration.getAlternativeJrePath() : null;
        JavaParametersUtil.configureModule(module, params, classPathType, jreHome);
        params.setMainClass(configuration.getRunClass());
        PathsList classPath = params.getClassPath();

//        CommandLineTargetBuilder cmdBuilder = new CommandLineTargetBuilder(configuration, params);
//        invokeSSH(classPath.getPathList().get(classPath.getPathList().size() - 1), cmdBuilder);
        return params;
    }

    /**
     * Executes Required SSH Commands
     * @param projectOutput
     * @param builder
     */
    private void invokeSSH(String projectOutput, CommandLineTargetBuilder builder) {
        RaspberryPIRunnerParameters runnerParameters = configuration.getRunnerParameters();
        SSHUploader uploader = new SSHUploader(getEnvironment().getProject());
        try {
            uploader.uploadToTarget(runnerParameters, new File(projectOutput), builder.buildCommandLine());
        } catch (Exception e) {
        }
    }

}