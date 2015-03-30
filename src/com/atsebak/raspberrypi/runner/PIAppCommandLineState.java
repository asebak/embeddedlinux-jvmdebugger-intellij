package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.protocol.ssh.CommandLineTarget;
import com.atsebak.raspberrypi.protocol.ssh.SSHUploader;
import com.atsebak.raspberrypi.runner.conf.RaspberryPIRunConfiguration;
import com.atsebak.raspberrypi.runner.data.RaspberryPIRunnerParameters;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.DebuggingRunnerData;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.PathsList;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PIAppCommandLineState extends JavaCommandLineState {
    private final RaspberryPIRunConfiguration configuration;
    private final ExecutionEnvironment environment;
    private final RunnerSettings runnerSettings;
    private boolean isDebugMode;

    public PIAppCommandLineState(@NotNull ExecutionEnvironment environment, RaspberryPIRunConfiguration configuration) {
        super(environment);
        this.configuration = configuration;
        this.environment = environment;
        this.runnerSettings = environment.getRunnerSettings();
        isDebugMode = runnerSettings instanceof DebuggingRunnerData;
//        addConsoleFilters(new PIConsoleFilter(getEnvironment().getProject()));
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
        JavaParameters javaParams = new JavaParameters();
        Project project = this.environment.getProject();
        ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());
        // All modules to use the same things
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules != null && modules.length > 0) {
            for (Module module : modules) {
                javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES);
            }
        }
        javaParams.setMainClass(this.configuration.getRunnerParameters().getMainclass());
        String basePath = project.getBasePath();
        javaParams.setWorkingDirectory(basePath);
        String classes = this.configuration.getOutputFilePath();
        javaParams.getProgramParametersList().addParametersString(classes);
        PathsList classPath = javaParams.getClassPath();

        CommandLineTarget build = CommandLineTarget.builder()
                .raspberryPIRunConfiguration(configuration)
                .isDebugging(isDebugMode)
                .parameters(javaParams).build();
        invokeSSH(classPath.getPathList().get(classPath.getPathList().size() - 1), build);
        return javaParams;
    }

    /**
     * Executes Required SSH Commands
     * @param projectOutput
     * @param builder
     */
    private void invokeSSH(String projectOutput, CommandLineTarget builder) {
        RaspberryPIRunnerParameters runnerParameters = configuration.getRunnerParameters();
        SSHUploader uploader = SSHUploader.builder().project(getEnvironment().getProject()).rp(runnerParameters).build();
        try {
            uploader.uploadToTarget(new File(projectOutput), builder.toCommand());
        } catch (Exception e) {
            final Notification notification = new Notification(
                    com.atsebak.raspberrypi.utils.Notifications.GROUPDISPLAY_ID, "SSH Connection Error", e.getLocalizedMessage(),
                    NotificationType.ERROR);
            Notifications.Bus.notify(notification);
        }
    }

}
