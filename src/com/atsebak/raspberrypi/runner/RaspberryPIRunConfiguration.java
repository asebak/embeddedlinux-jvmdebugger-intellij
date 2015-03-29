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
import java.util.Map;

public class RaspberryPIRunConfiguration extends ModuleBasedConfiguration<JavaRunConfigurationModule> implements CommonJavaRunConfigurationParameters,
        SingleClassConfiguration, RefactoringListenerProvider {
    private RaspberryPIRunnerParameters raspberryPIRunnerParameters = new RaspberryPIRunnerParameters();

    /**
     * Run Configurations To Run App
     *
     * @param project
     * @param factory
     */
    protected RaspberryPIRunConfiguration(final Project project, final ConfigurationFactory factory) {
        super(new JavaRunConfigurationModule(project, false), factory);
    }

    /**
     * Settings Editor
     * @return
     */
    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        SettingsEditorGroup<RaspberryPIRunConfiguration> group = new SettingsEditorGroup<RaspberryPIRunConfiguration>();
        group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new RaspberryPIRunConfigurationEditor(getProject()));
        JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
        group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<RaspberryPIRunConfiguration>());
        return group;
    }

    /**
     * Creates new running paramters instance
     * @return
     */
    protected RaspberryPIRunnerParameters createRunnerParametersInstance() {
        return new RaspberryPIRunnerParameters();
    }

    /**
     * All modules are valid in the project
     * @return
     */
    @Override
    public Collection<Module> getValidModules() {
        return JavaRunConfigurationModule.getModulesForClass(this.getProject(), this.getRunnerParameters().getMainclass());
    }

    /**
     * Read External
     * @param element
     * @throws InvalidDataException
     */
    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        raspberryPIRunnerParameters = createRunnerParametersInstance();
        XmlSerializer.deserializeInto(raspberryPIRunnerParameters, element);
    }

    /**
     * Write External
     * @param element
     * @throws WriteExternalException
     */
    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        if (raspberryPIRunnerParameters != null) {
            XmlSerializer.serializeInto(raspberryPIRunnerParameters, element);
        }
    }

    /**
     * Gets the state of the execution environment
     * @param executor
     * @param env
     * @return
     * @throws ExecutionException
     */
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        final JavaCommandLineState state = new RemoteJavaApplicationCommandLineState(this, env);
        final TextConsoleBuilder textConsoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(getProject());
        textConsoleBuilder.setViewer(true);
        state.setConsoleBuilder(textConsoleBuilder);
        return state;
    }


    /**
     * Checks weather all the supplied paramters from the user are correct
     * @throws RuntimeConfigurationException
     */
    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        RaspberryPiRunnerValidator.checkJavaSettings(this);
        RaspberryPiRunnerValidator.checkPiSettings(getRunnerParameters());
    }

    /**
     * Gets runner paramters instance
     * @return
     */
    public RaspberryPIRunnerParameters getRunnerParameters() {
        return raspberryPIRunnerParameters;
    }

    @Nullable
    public PsiClass getMainClass() {
        return this.getConfigurationModule().findClass(this.getRunnerParameters().getMainclass());
    }

    public void setMainClass(PsiClass var1) {
        Module var2 = this.getConfigurationModule().getModule();
        this.setMainClassName(JavaExecutionUtil.getRuntimeQualifiedName(var1));
        this.setModule(JavaExecutionUtil.findModule(var1));
        this.restoreOriginalModule(var2);
    }

    @Nullable
    public String suggestedName() {
        return this.getRunnerParameters().getMainclass() == null ?
                null
                : JavaExecutionUtil.getPresentableClassName(this.getRunnerParameters().getMainclass());
    }

    public String getActionName() {
        return this.getRunnerParameters().getMainclass()
                != null && this.getRunnerParameters().getMainclass().length()
                != 0 ? ProgramRunnerUtil.shortenName(JavaExecutionUtil.getShortClassName(this.getRunnerParameters().getMainclass()), 6) + ".main()" : null;
    }

    public void setMainClassName(String var1) {
        this.getRunnerParameters().setMainclass(var1);
    }

    @Override
    public String getVMParameters() {
        return this.getRunnerParameters().getVmParameters();
    }

    @Override
    public void setVMParameters(String var1) {
        this.getRunnerParameters().setVmParameters(var1);
    }

    @Override
    public boolean isAlternativeJrePathEnabled() {
        return this.getRunnerParameters().isAlternateJrePathEnabled();
    }

    @Override
    public void setAlternativeJrePathEnabled(boolean var1) {
        this.getRunnerParameters().setAlternateJrePathEnabled(var1);
    }

    @Override
    public String getAlternativeJrePath() {
        return this.getRunnerParameters().getAlternateJrePath();
    }

    @Override
    public void setAlternativeJrePath(String s) {
        this.getRunnerParameters().setAlternateJrePath(s);
    }

    @Nullable
    @Override
    public String getRunClass() {
        return this.getRunnerParameters().getMainclass();
    }

    @Nullable
    @Override
    public String getPackage() {
        return null;
    }

    @Nullable
    @Override
    public String getProgramParameters() {
        return this.getRunnerParameters().getProgramParameters();
    }

    @Override
    public void setProgramParameters(@Nullable String var1) {
        this.getRunnerParameters().setProgramParameters(var1);
    }

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return ExternalizablePath.localPathValue(this.getRunnerParameters().getWorkingDirectory());
    }

    @Override
    public void setWorkingDirectory(@Nullable String s) {
        this.getRunnerParameters().setWorkingDirectory(ExternalizablePath.urlValue(s));
    }

    @NotNull
    @Override
    public Map<String, String> getEnvs() {
        return this.getRunnerParameters().getEnvs();
    }

    @Override
    public void setEnvs(@NotNull final Map<String, String> envs) {
        this.getRunnerParameters().getEnvs().clear();
        this.getRunnerParameters().getEnvs().putAll(envs);
    }

    @Override
    public boolean isPassParentEnvs() {
        return this.getRunnerParameters().isPassParentEnv();
    }

    @Override
    public void setPassParentEnvs(boolean b) {
        this.getRunnerParameters().setPassParentEnv(b);
    }

    public boolean isEnableSwingInspector() {
        return this.getRunnerParameters().isEnableSwingInspector();
    }

    public void setEnableSwingInspector(boolean enableSwingInspector) {
        this.getRunnerParameters().setEnableSwingInspector(enableSwingInspector);
    }

    @Nullable
    @Override
    public RefactoringElementListener getRefactoringElementListener(PsiElement psiElement) {
        RefactoringElementListener var2 = RefactoringListeners.getClassOrPackageListener(psiElement, new RefactoringListeners.SingleClassConfigurationAccessor(this));
        return RunConfigurationExtension.wrapRefactoringElementListener(psiElement, this, var2);
    }
}

