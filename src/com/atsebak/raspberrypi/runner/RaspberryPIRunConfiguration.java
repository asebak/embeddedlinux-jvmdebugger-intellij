package com.atsebak.raspberrypi.runner;

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.*;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.junit.RefactoringListeners;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiMethodUtil;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.util.PathsList;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

//ApplicationConfiguration RunConfigurationBase

public class RaspberryPIRunConfiguration extends ModuleBasedConfiguration<JavaRunConfigurationModule> implements CommonJavaRunConfigurationParameters, SingleClassConfiguration, RefactoringListenerProvider {
    public static final String DEFAULT_HOSTNAME = "0.0.0.0";
    public static final String DEFAULT_DEBUG_POST = "4000";
    public static final boolean DEFAULT_RUN_AS_ROOT = true;
    public static final String DEFAULT_DISPLAY = ":0";
    public String MAIN_CLASS_NAME;
    public String VM_PARAMETERS;
    public String PROGRAM_PARAMETERS;
    public String WORKING_DIRECTORY;
    public boolean ALTERNATIVE_JRE_PATH_ENABLED;
    public String ALTERNATIVE_JRE_PATH;
    public boolean ENABLE_SWING_INSPECTOR;
    public String ENV_VARIABLES;
    private final Map<String,String> myEnvs = new LinkedHashMap<String, String>();
    public boolean PASS_PARENT_ENVS;

    private RaspberryPIRunnerParameters raspberryPIRunnerParameters = new RaspberryPIRunnerParameters();
    private Project project;
    protected RaspberryPIRunConfiguration(final Project project, final ConfigurationFactory factory) {
        super(new JavaRunConfigurationModule(project, false), factory);
        this.project = project;
    }

    public static void checkURL(String url) throws RuntimeConfigurationException {
        // check URL for correctness
        try {
            if (url == null) {
                throw new MalformedURLException("No start file specified or this file is invalid");
            }
            new URL(url);
        } catch (MalformedURLException ignored) {
            throw new RuntimeConfigurationError("Incorrect URL");
        }
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        SettingsEditorGroup<RaspberryPIRunConfiguration> group = new SettingsEditorGroup<RaspberryPIRunConfiguration>();
        group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new RaspberryPIRunConfigurationEditor(getProject()));
        JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
        group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<RaspberryPIRunConfiguration>());
        return group;
    }

    protected RaspberryPIRunnerParameters createRunnerParametersInstance() {
        return new RaspberryPIRunnerParameters();
    }

    @Override
    public Collection<Module> getValidModules() {
        return JavaRunConfigurationModule.getModulesForClass(this.getProject(), this.MAIN_CLASS_NAME);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        raspberryPIRunnerParameters = createRunnerParametersInstance();
        XmlSerializer.deserializeInto(raspberryPIRunnerParameters, element);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        if (raspberryPIRunnerParameters != null) {
            XmlSerializer.serializeInto(raspberryPIRunnerParameters, element);
        }
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        final JavaCommandLineState state = new RemoteJavaApplicationCommandLineState(this, env);
        JavaRunConfigurationModule module = getConfigurationModule();
        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject(), module.getSearchScope()));
        return state;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        //todo validate configuration here
//        checkURL(raspberryPIRunnerParameters.getHostname());
        checkJavaSettings();
    }

    private void checkJavaSettings() throws RuntimeConfigurationException {
        JavaParametersUtil.checkAlternativeJRE(this);
        JavaRunConfigurationModule var1 = this.getConfigurationModule();
        PsiClass var2 = var1.checkModuleAndClassName(this.MAIN_CLASS_NAME, ExecutionBundle.message("no.main.class.specified.error.text", new Object[0]));
        if (!PsiMethodUtil.hasMainMethod(var2)) {
            throw new RuntimeConfigurationWarning(ExecutionBundle.message("main.method.not.found.in.class.error.message", new Object[]{this.MAIN_CLASS_NAME}));
        } else {
            ProgramParametersUtil.checkWorkingDirectoryExist(this, this.getProject(), var1.getModule());
            JavaRunConfigurationExtensionManager.checkConfigurationIsValid(this);
        }
    }

    public RaspberryPIRunnerParameters getRunnerParameters() {
        return raspberryPIRunnerParameters;
    }

    public void setMainClass(PsiClass var1) {
        Module var2 = this.getConfigurationModule().getModule();
        this.setMainClassName(JavaExecutionUtil.getRuntimeQualifiedName(var1));
        this.setModule(JavaExecutionUtil.findModule(var1));
        this.restoreOriginalModule(var2);
    }

    @Nullable
    public PsiClass getMainClass() {
        return ((JavaRunConfigurationModule)this.getConfigurationModule()).findClass(this.MAIN_CLASS_NAME);
    }

    @Nullable
    public String suggestedName() {
        return this.MAIN_CLASS_NAME == null?null:JavaExecutionUtil.getPresentableClassName(this.MAIN_CLASS_NAME);
    }

    public String getActionName() {
        return this.MAIN_CLASS_NAME != null && this.MAIN_CLASS_NAME.length() != 0?ProgramRunnerUtil.shortenName(JavaExecutionUtil.getShortClassName(this.MAIN_CLASS_NAME), 6) + ".main()":null;
    }

    public void setMainClassName(String var1) {
        this.MAIN_CLASS_NAME = var1;
    }

    @Override
    public void setVMParameters(String var1) {
        this.VM_PARAMETERS = var1;
    }

    @Override
    public String getVMParameters() {
        return this.VM_PARAMETERS;
    }

    @Override
    public boolean isAlternativeJrePathEnabled() {
        return this.ALTERNATIVE_JRE_PATH_ENABLED;
    }

    @Override
    public void setAlternativeJrePathEnabled(boolean var1) {
        this.ALTERNATIVE_JRE_PATH_ENABLED = var1;

    }

    @Override
    public String getAlternativeJrePath() {
        return this.ALTERNATIVE_JRE_PATH;
    }

    @Override
    public void setAlternativeJrePath(String s) {
        this.ALTERNATIVE_JRE_PATH = s;
    }

    @Nullable
    @Override
    public String getRunClass() {
        return this.MAIN_CLASS_NAME;
    }

    @Nullable
    @Override
    public String getPackage() {
        return null;
    }

    @Override
    public void setProgramParameters(@Nullable String var1) {
        this.PROGRAM_PARAMETERS = var1;
    }

    @Nullable
    @Override
    public String getProgramParameters() {
        return this.PROGRAM_PARAMETERS;
    }

    @Override
    public void setWorkingDirectory(@Nullable String s) {
        this.WORKING_DIRECTORY = ExternalizablePath.urlValue(s);
    }

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return ExternalizablePath.localPathValue(this.WORKING_DIRECTORY);
    }

    @Override
    public void setEnvs(@NotNull final Map<String, String> envs) {
        myEnvs.clear();
        myEnvs.putAll(envs);
    }

    @NotNull
    @Override
    public Map<String, String> getEnvs() {
        return myEnvs;
    }

    @Override
    public void setPassParentEnvs(boolean b) {
        this.PASS_PARENT_ENVS = b;
    }

    @Override
    public boolean isPassParentEnvs() {
        return this.PASS_PARENT_ENVS;
    }

    @Nullable
    @Override
    public RefactoringElementListener getRefactoringElementListener(PsiElement psiElement) {
        RefactoringElementListener var2 = RefactoringListeners.getClassOrPackageListener(psiElement, new RefactoringListeners.SingleClassConfigurationAccessor(this));
        return RunConfigurationExtension.wrapRefactoringElementListener(psiElement, this, var2);
    }

    /** Class to configure command line state of the dev app server **/
    public static class RemoteJavaApplicationCommandLineState extends JavaCommandLineState {

        private final RaspberryPIRunConfiguration configuration;

        public RemoteJavaApplicationCommandLineState(@NotNull final RaspberryPIRunConfiguration configuration,
                                               final ExecutionEnvironment environment) {
            super(environment);
            this.configuration = configuration;
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
//            setupJavaParameters(params);

            return params;
//            final JavaParameters params = new JavaParameters();
//            final JavaRunConfigurationModule module = configuration.getConfigurationModule();
//            params.setMainClass();
//            return params;
            //todo create proper parameters
//            params.setMainClass();
//            params.setMainClass(AppEngineSdk.DEV_APPSERVER_CLASS);
//
//            AppEngineSdk sdk = new AppEngineSdk(configuration.mySdkPath);
//
//            ParametersList vmParams = params.getVMParametersList();
//            if (configuration.myVmArgs != null && !configuration.myVmArgs.trim().isEmpty()) {
//                vmParams.addParametersString(configuration.myVmArgs);
//            }
//            sdk.addServerVmParams(vmParams);
//
//            ParametersList programParams = params.getProgramParametersList();
//            if (configuration.myServerAddress != null && !configuration.myServerAddress.trim().isEmpty()) {
//                programParams.add("--address=" + configuration.myServerAddress);
//            }
//            if (configuration.myServerPort != null && !configuration.myServerPort.trim().isEmpty()) {
//                programParams.add("--port=" + configuration.myServerPort);
//            }
//
//            String warPath = configuration.myWarPath;
//            if (warPath == null) {
//                throw new ExecutionException("War path is invalid");
//            }
//            programParams.add(warPath);
//
//            params.setWorkingDirectory(warPath);
//            PathsList classPath = params.getClassPath();
//
//            classPath.add(sdk.getToolsApiJarFile().getAbsolutePath());
//
//            Module appEngineModule = module.getModule();
//            if (appEngineModule == null) {
//                throw new ExecutionException("Module not defined");
//            }
//
//            //TODO : allow selectable alternate jre
//            params.setJdk(JavaParameters.getModuleJdk(appEngineModule));
//
//            UsageTracker.getInstance()
//                    .trackEvent(GctTracking.CATEGORY, GctTracking.RUN, configuration.getSyncWithGradle() ? "sync" : "custom", null);
//            return params;
        }
    }
}
