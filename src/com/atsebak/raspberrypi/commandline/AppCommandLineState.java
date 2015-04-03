package com.atsebak.raspberrypi.commandline;

import com.atsebak.raspberrypi.console.PIConsoleView;
import com.atsebak.raspberrypi.console.PIOutputForwarder;
import com.atsebak.raspberrypi.deploy.DeploymentTarget;
import com.atsebak.raspberrypi.protocol.ssh.SSHBuilder;
import com.atsebak.raspberrypi.protocol.ssh.SSHHandlerTarget;
import com.atsebak.raspberrypi.runner.conf.RaspberryPIRunConfiguration;
import com.atsebak.raspberrypi.runner.data.RaspberryPIRunnerParameters;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.*;
import com.intellij.execution.remote.RemoteConfiguration;
import com.intellij.execution.remote.RemoteConfigurationType;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.javadoc.JavadocBundle;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.util.NotNullFunction;
import com.intellij.util.PathsList;
import net.schmizz.sshj.SSHClient;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;

public class AppCommandLineState extends JavaCommandLineState {
    @NonNls
    private static final String RUN_CONFIGURATION_NAME_PATTERN = "PI Debugger (%s)";
    @NonNls
    private static final String DEBUG_TCP_MESSAGE = "Listening for transport dt_socket at address: %s";
    private final RaspberryPIRunConfiguration configuration;
    private final ExecutionEnvironment environment;
    private final RunnerSettings runnerSettings;
    private boolean isDebugMode;
    private PIOutputForwarder outputForwarder;

    /**
     * Command line state when runner is launch
     *
     * @param environment
     * @param configuration
     */
    public AppCommandLineState(@NotNull ExecutionEnvironment environment, @NotNull RaspberryPIRunConfiguration configuration) {
        super(environment);
        this.configuration = configuration;
        this.environment = environment;
        this.runnerSettings = environment.getRunnerSettings();
        isDebugMode = runnerSettings instanceof DebuggingRunnerData;
        outputForwarder = new PIOutputForwarder(PIConsoleView.getInstance(environment.getProject()));
        outputForwarder.attachTo(null);
    }

    /**
     * Gets the debug runner
     *
     * @param debugPort
     * @return
     */
    @NotNull
    public static String getRunConfigurationName(String debugPort) {
        return String.format(RUN_CONFIGURATION_NAME_PATTERN, debugPort);
    }

    /**
     * Called when either debug or run mode executed, overrides console with a new handler
     * @param executor
     * @param runner
     * @return
     * @throws ExecutionException
     */
    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        OSProcessHandler handler = this.startProcess();
        final TextConsoleBuilder textConsoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(getEnvironment().getProject());
        textConsoleBuilder.setViewer(true);
        textConsoleBuilder.getConsole().attachToProcess(handler);
        return new DefaultExecutionResult(textConsoleBuilder.getConsole(), handler);
    }

    /**
     * Starts console process
     * @return
     * @throws ExecutionException
     */
    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        final OSProcessHandler handler = JavaCommandLineStateUtil.startProcess(createCommandLine());
        ProcessTerminatedListener.attach(handler, configuration.getProject(), JavadocBundle.message("javadoc.generate.exited"));
        handler.addProcessListener(new ProcessAdapter() {
            @Override
            public void startNotified(ProcessEvent event) {
                super.startNotified(event);
            }

            @Override
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                super.onTextAvailable(event, outputType);
            }

            @Override
            public void processTerminated(ProcessEvent event) {
                super.processTerminated(event);
            }

            @Override
            public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
                super.processWillTerminate(event, willBeDestroyed);
            }
        });
        return handler;
    }

    /**
     * Creates the necessary Java paramaters for the application.
     *
     * @return
     * @throws ExecutionException
     */
    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        PIConsoleView.getInstance(environment.getProject()).clear();
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
        final PathsList classPath = javaParams.getClassPath();

        final CommandLineTarget build = CommandLineTarget.builder()
                .raspberryPIRunConfiguration(configuration)
                .isDebugging(isDebugMode)
                .parameters(javaParams).build();
        invokeDeployment(classPath.getPathList().get(classPath.getPathList().size() - 1), build);
        if (isDebugMode) {
            final String initializeMsg = String.format(DEBUG_TCP_MESSAGE, configuration.getRunnerParameters().getPort());
            //this should wait until the deployment states that it's listening to the port
            while (!outputForwarder.toString().contains(initializeMsg)) {
            }
            closeOldSessionAndDebug(project, configuration.getRunnerParameters());
        }
        return javaParams;
    }

    /**
     * Executes Deploys and Runs App on remote target
     * @param projectOutput
     * @param commandLineTarget
     */
    private void invokeDeployment(String projectOutput, CommandLineTarget commandLineTarget) {
        PIConsoleView.getInstance(environment.getProject()).print("Deploying to the Raspberry PI\n\r", ConsoleViewContentType.SYSTEM_OUTPUT);
        RaspberryPIRunnerParameters runnerParameters = configuration.getRunnerParameters();

        DeploymentTarget target = DeploymentTarget.builder()
                .sshHandlerTarget(SSHHandlerTarget.builder()
                        .piRunnerParameters(runnerParameters)
                        .consoleView(PIConsoleView.getInstance(getEnvironment().getProject()))
                        .sshBuilder(SSHBuilder
                                .builder()
                                .sshClient(new SSHClient())
                                .connectionTimeout(3000)
                                .timeout(3000)
                                .build()).build()).build();
        try {
            target.upload(new File(projectOutput), commandLineTarget.toString());
        } catch (Exception e) {
            PIConsoleView.getInstance(environment.getProject()).print("Cannot connect to the Raspberry PI: " + e.getLocalizedMessage(),
                    ConsoleViewContentType.ERROR_OUTPUT);
            throw new RuntimeException("Cannot deploy to remote device");
        }
    }

    /**
     * Creates debugging settings for server
     *
     * @param project
     * @param debugPort
     * @param hostname
     * @return
     */
    private RunnerAndConfigurationSettings createRunConfiguration(Project project, String debugPort, String hostname) {
        final RemoteConfigurationType remoteConfigurationType = RemoteConfigurationType.getInstance();

        final ConfigurationFactory factory = remoteConfigurationType.getFactory();
        final RunnerAndConfigurationSettings runSettings =
                RunManager.getInstance(project).createRunConfiguration(getRunConfigurationName(debugPort), factory);
        final RemoteConfiguration configuration = (RemoteConfiguration) runSettings.getConfiguration();

        configuration.HOST = hostname;
        configuration.PORT = debugPort;
        configuration.USE_SOCKET_TRANSPORT = true;
        configuration.SERVER_MODE = false;

        return runSettings;
    }

    /**
     * Closes an old descriptor and creates a new one in debug mode connecting to remote target
     *
     * @param project
     * @param parameters
     */
    private void closeOldSessionAndDebug(final Project project, RaspberryPIRunnerParameters parameters) {
        final String configurationName = getRunConfigurationName(parameters.getPort());
        final Collection<RunContentDescriptor> descriptors =
                ExecutionHelper.findRunningConsoleByTitle(project, new NotNullFunction<String, Boolean>() {
                    @NotNull
                    @Override
                    public Boolean fun(String title) {
                        return configurationName.equals(title);
                    }
                });

        if (descriptors.size() > 0) {
            final RunContentDescriptor descriptor = descriptors.iterator().next();
            final ProcessHandler processHandler = descriptor.getProcessHandler();
            final Content content = descriptor.getAttachedContent();

            if (processHandler != null && content != null) {
                final Executor executor = DefaultDebugExecutor.getDebugExecutorInstance();

                if (processHandler.isProcessTerminated()) {
                    ExecutionManager.getInstance(project).getContentManager()
                            .removeRunContent(executor, descriptor);
                } else {
                    content.getManager().setSelectedContent(content);
                    ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(executor.getToolWindowId());
                    window.activate(null, false, true);
                    return;
                }
            }
        }
        runSession(project, parameters);
    }

    /**
     * Runs in remote debug mode using that executioner
     *
     * @param project
     * @param parameters
     */
    private void runSession(final Project project, RaspberryPIRunnerParameters parameters) {
        final RunnerAndConfigurationSettings settings = createRunConfiguration(project, parameters.getPort(), parameters.getHostname());
        ProgramRunnerUtil.executeConfiguration(project, settings, DefaultDebugExecutor.getDebugExecutorInstance());
    }


}
