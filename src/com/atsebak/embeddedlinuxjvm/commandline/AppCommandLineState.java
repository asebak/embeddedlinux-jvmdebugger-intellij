package com.atsebak.embeddedlinuxjvm.commandline;

import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMConsoleView;
import com.atsebak.embeddedlinuxjvm.console.EmbeddedLinuxJVMOutputForwarder;
import com.atsebak.embeddedlinuxjvm.deploy.DeploymentTarget;
import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSHHandlerTarget;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch.EmbeddedSSHClient;
import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.atsebak.embeddedlinuxjvm.services.ClasspathService;
import com.atsebak.embeddedlinuxjvm.utils.FileUtilities;
import com.atsebak.embeddedlinuxjvm.utils.RemoteCommandLineBuilder;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.*;
import com.intellij.execution.remote.RemoteConfiguration;
import com.intellij.execution.remote.RemoteConfigurationType;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.util.NotNullFunction;
import com.intellij.util.PathsList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AppCommandLineState extends JavaCommandLineState {
    @NonNls
    private static final String RUN_CONFIGURATION_NAME_PATTERN = "Embedded Device Debugger (%s)";
    @NonNls
    private static final String DEBUG_TCP_MESSAGE = "Listening for transport dt_socket at address: %s";
    @NotNull
    private final EmbeddedLinuxJVMRunConfiguration configuration;
    @NotNull
    private final RunnerSettings runnerSettings;
    @NotNull
    private final EmbeddedLinuxJVMOutputForwarder outputForwarder;
    @NotNull
    private final boolean isDebugMode;
    @NotNull
    private final Project project;

    /**
     * Command line state when runner is launch
     *
     * @param environment
     * @param configuration
     */
    public AppCommandLineState(@NotNull ExecutionEnvironment environment, @NotNull EmbeddedLinuxJVMRunConfiguration configuration) {
        super(environment);
        this.configuration = configuration;
        this.runnerSettings = environment.getRunnerSettings();
        this.project = environment.getProject();
        this.isDebugMode = runnerSettings instanceof DebuggingRunnerData;
        this.outputForwarder = new EmbeddedLinuxJVMOutputForwarder(EmbeddedLinuxJVMConsoleView.getInstance(project));
        this.outputForwarder.attachTo(null);
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
     * Creates the console view
     * @param executor
     * @return
     * @throws ExecutionException
     */
    @Nullable
    @Override
    protected ConsoleView createConsole(@NotNull Executor executor) throws ExecutionException {
        return EmbeddedLinuxJVMConsoleView.getInstance(project).getConsoleView(true);
    }

    /**
     * Creates the command line view
     * @return
     * @throws ExecutionException
     */
    @Override
    protected GeneralCommandLine createCommandLine() throws ExecutionException {
        return RemoteCommandLineBuilder.createFromJavaParameters(getJavaParameters(), CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext()), true);
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
        ProcessTerminatedListener.attach(handler, project, EmbeddedLinuxJVMBundle.message("pi.console.exited"));
        handler.addProcessListener(new ProcessAdapter() {
            private void closeSSHConnection() {
                try {
                    if (isDebugMode) {
                        //todo fix tcp connection closing issue random error message showing up
                        final DebuggerSession debuggerSession = DebuggerManagerEx.getInstanceEx(project).getContext().getDebuggerSession();
                        if (debuggerSession == null) {
                            return;
                        }

                        final DebugProcessImpl debugProcess = debuggerSession.getProcess();
                        if (debugProcess.isDetached() || debugProcess.isDetaching()) {
                            debugProcess.stop(true);
                            debugProcess.dispose();
                            debuggerSession.dispose();
                        }
                    }
                } catch (Exception e) {
                }
            }

            /**
             * closes debug session
             */
            private void closeDescriptors() {
                //todo remove remote debugger console
                final Collection<RunContentDescriptor> descriptors =
                        ExecutionHelper.findRunningConsoleByTitle(project, new NotNullFunction<String, Boolean>() {
                            @NotNull
                            @Override
                            public Boolean fun(String title) {
                                return AppCommandLineState.getRunConfigurationName(configuration.getRunnerParameters().getPort()).equals(title);
                            }
                        });
//                for (RunContentDescriptor descriptor : descriptors) {
//                    final Content content = descriptor.getAttachedContent();
//                }
            }

            /**
             * Called when user clicks the stop button
             *
             * @param event
             * @param willBeDestroyed
             */
            @Override
            public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
                ProgressManager.getInstance().run(new Task.Backgroundable(project, EmbeddedLinuxJVMBundle.message("pi.closingsession"), true) {
                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {
                        closeSSHConnection();
                        if (isDebugMode) {
                            closeDescriptors();
                        }
                    }
                });
                super.processWillTerminate(event, willBeDestroyed);
            }

            @Override
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                super.onTextAvailable(event, outputType);
            }

        });
        return handler;
    }

    /**
     * Creates the necessary Java parameters for the application.
     *
     * @return
     * @throws ExecutionException
     */
    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        EmbeddedLinuxJVMConsoleView.getInstance(project).clear();
        JavaParameters javaParams = new JavaParameters();
        final ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());

        // All modules to use the same things
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length > 0) {
            for (Module module : modules) {
                javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES);
            }
        }
        // setting jvm config
        javaParams.getProgramParametersList().add(configuration.getRunnerParameters().getProgramArguments());
        javaParams.getVMParametersList().add(configuration.getRunnerParameters().getVmParameters());
        javaParams.setMainClass(configuration.getRunnerParameters().getMainclass());
        javaParams.setWorkingDirectory(project.getBasePath());
        javaParams.getProgramParametersList().addParametersString(configuration.getOutputFilePath());

        final CommandLineTarget commandLineTarget = CommandLineTarget.builder()
                .embeddedLinuxJVMRunConfiguration(configuration)
                .isDebugging(isDebugMode)
                .parameters(javaParams).build();

        final PathsList classPath = javaParams.getClassPath();

        final Application app = ApplicationManager.getApplication();

        //deploy on Non-read thread so can execute right away
        app.executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ClasspathService service = ServiceManager.getService(project, ClasspathService.class);
                            List<File> hostLibraries = invokeClassPathResolver(classPath.getPathList(), manager.getProjectSdk());
                            File classpathArchive = FileUtilities.createClasspathArchive(service.deltaOfDeployedJars(hostLibraries), project);
                            invokeDeployment(classpathArchive.getPath(), commandLineTarget);
                        } catch (Exception e) {
                            EmbeddedLinuxJVMConsoleView.getInstance(project).print(EmbeddedLinuxJVMBundle.message("pi.connection.failed", e.getMessage()) + "\r\n",
                                    ConsoleViewContentType.ERROR_OUTPUT);
                            //todo should cancel application
                        }
                    }
                });
            }
        });

        //invoke later because it reads from other threads(debugging executor)
        ProgressManager.getInstance().run(new Task.Backgroundable(project, EmbeddedLinuxJVMBundle.message("pi.deploy"), true) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                if (isDebugMode) {
                    progressIndicator.setIndeterminate(true);
                    final String initializeMsg = String.format(DEBUG_TCP_MESSAGE, configuration.getRunnerParameters().getPort());
                    //this should wait until the deployment states that it's listening to the port
                    while (!outputForwarder.toString().contains(initializeMsg)) {
                    }
                    app.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            closeOldSessionAndDebug(project, configuration.getRunnerParameters());
                        }
                    });
                }
            }
        });

        return javaParams;
    }

    private List<File> invokeClassPathResolver(List<String> librariesNeeded, final Sdk sdk) {
        List<File> classPaths = new ArrayList<File>();
        VirtualFile homeDirectory = sdk.getHomeDirectory();
        for (String library : librariesNeeded) {
            //filter sdk libraries from classpath because it's too big
            if (!library.contains(homeDirectory.getPath())) {
                classPaths.add(new File(library));
            }
        }
        return classPaths;
    }

    /**
     * Executes Deploys and Runs App on remote target
     * @param projectOutput
     * @param commandLineTarget
     */
    private void invokeDeployment(String projectOutput, CommandLineTarget commandLineTarget) throws RuntimeConfigurationException, IOException, ClassNotFoundException {
        EmbeddedLinuxJVMConsoleView.getInstance(project).print(EmbeddedLinuxJVMBundle.getString("pi.deployment.start"), ConsoleViewContentType.SYSTEM_OUTPUT);
        EmbeddedLinuxJVMRunConfigurationRunnerParameters runnerParameters = configuration.getRunnerParameters();

        DeploymentTarget target = DeploymentTarget.builder()
                .sshHandlerTarget(SSHHandlerTarget.builder()
                        .params(runnerParameters)
                        .consoleView(EmbeddedLinuxJVMConsoleView.getInstance(project))
                        .ssh(EmbeddedSSHClient.builder()
                                .hostname(runnerParameters.getHostname())
                                .password(runnerParameters.getPassword())
                                .username(runnerParameters.getUsername())
                                .useKey(runnerParameters.isUsingKey())
                                .key(runnerParameters.getKeyPath())
                                .build())
                        .build())
                .build();
        target.upload(new File(projectOutput), commandLineTarget.toString());
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
     * Closes old session only for debug mode
     *
     * @param project
     * @param parameters
     */
    private void closeOldSession(final Project project, EmbeddedLinuxJVMRunConfigurationRunnerParameters parameters) {
        final String configurationName = AppCommandLineState.getRunConfigurationName(parameters.getPort());
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
    }

    /**
     * Closes an old descriptor and creates a new one in debug mode connecting to remote target
     *
     * @param project
     * @param parameters
     */
    private void closeOldSessionAndDebug(final Project project, EmbeddedLinuxJVMRunConfigurationRunnerParameters parameters) {
        closeOldSession(project, parameters);
        runSession(project, parameters);
    }

    /**
     * Runs in remote debug mode using that executioner
     *
     * @param project
     * @param parameters
     */
    private void runSession(final Project project, EmbeddedLinuxJVMRunConfigurationRunnerParameters parameters) {
        final RunnerAndConfigurationSettings settings = createRunConfiguration(project, parameters.getPort(), parameters.getHostname());
        ProgramRunnerUtil.executeConfiguration(project, settings, DefaultDebugExecutor.getDebugExecutorInstance());
    }


}
