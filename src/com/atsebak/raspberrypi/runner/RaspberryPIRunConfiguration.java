package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.ui.RaspberryPIRunConfigurationEditor;
import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.junit.RefactoringListeners;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class RaspberryPIRunConfiguration extends ModuleBasedConfiguration<JavaRunConfigurationModule> implements CommonJavaRunConfigurationParameters,
        SingleClassConfiguration, RefactoringListenerProvider {
    private final Map<String, String> myEnvs = new LinkedHashMap<String, String>();
    public String MAIN_CLASS_NAME;
    public String VM_PARAMETERS;
    public String PROGRAM_PARAMETERS;
    public String WORKING_DIRECTORY;
    public boolean ALTERNATIVE_JRE_PATH_ENABLED;
    public String ALTERNATIVE_JRE_PATH;
    public boolean ENABLE_SWING_INSPECTOR;
    public boolean PASS_PARENT_ENVS;
    private RaspberryPIRunnerParameters raspberryPIRunnerParameters = new RaspberryPIRunnerParameters();

    protected RaspberryPIRunConfiguration(final Project project, final ConfigurationFactory factory) {
        super(new JavaRunConfigurationModule(project, false), factory);
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
        final TextConsoleBuilder textConsoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(getProject());
        textConsoleBuilder.setViewer(true);
        state.setConsoleBuilder(textConsoleBuilder);
        return state;
    }


    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        RaspberryPiRunnerValidator.checkJavaSettings(this);
        RaspberryPiRunnerValidator.checkPiSettings(getRunnerParameters());
    }

    public RaspberryPIRunnerParameters getRunnerParameters() {
        return raspberryPIRunnerParameters;
    }

    @Nullable
    public PsiClass getMainClass() {
        return this.getConfigurationModule().findClass(this.MAIN_CLASS_NAME);
    }

    public void setMainClass(PsiClass var1) {
        Module var2 = this.getConfigurationModule().getModule();
        this.setMainClassName(JavaExecutionUtil.getRuntimeQualifiedName(var1));
        this.setModule(JavaExecutionUtil.findModule(var1));
        this.restoreOriginalModule(var2);
    }

    @Nullable
    public String suggestedName() {
        return this.MAIN_CLASS_NAME == null ? null : JavaExecutionUtil.getPresentableClassName(this.MAIN_CLASS_NAME);
    }

    public String getActionName() {
        return this.MAIN_CLASS_NAME != null && this.MAIN_CLASS_NAME.length() != 0 ? ProgramRunnerUtil.shortenName(JavaExecutionUtil.getShortClassName(this.MAIN_CLASS_NAME), 6) + ".main()" : null;
    }

    public void setMainClassName(String var1) {
        this.MAIN_CLASS_NAME = var1;
    }

    @Override
    public String getVMParameters() {
        return this.VM_PARAMETERS;
    }

    @Override
    public void setVMParameters(String var1) {
        this.VM_PARAMETERS = var1;
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

    @Nullable
    @Override
    public String getProgramParameters() {
        return this.PROGRAM_PARAMETERS;
    }

    @Override
    public void setProgramParameters(@Nullable String var1) {
        this.PROGRAM_PARAMETERS = var1;
    }

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return ExternalizablePath.localPathValue(this.WORKING_DIRECTORY);
    }

    @Override
    public void setWorkingDirectory(@Nullable String s) {
        this.WORKING_DIRECTORY = ExternalizablePath.urlValue(s);
    }

    @NotNull
    @Override
    public Map<String, String> getEnvs() {
        return myEnvs;
    }

    @Override
    public void setEnvs(@NotNull final Map<String, String> envs) {
        myEnvs.clear();
        myEnvs.putAll(envs);
    }

    @Override
    public boolean isPassParentEnvs() {
        return this.PASS_PARENT_ENVS;
    }

    @Override
    public void setPassParentEnvs(boolean b) {
        this.PASS_PARENT_ENVS = b;
    }

    @Nullable
    @Override
    public RefactoringElementListener getRefactoringElementListener(PsiElement psiElement) {
        RefactoringElementListener var2 = RefactoringListeners.getClassOrPackageListener(psiElement, new RefactoringListeners.SingleClassConfigurationAccessor(this));
        return RunConfigurationExtension.wrapRefactoringElementListener(psiElement, this, var2);
    }
}

