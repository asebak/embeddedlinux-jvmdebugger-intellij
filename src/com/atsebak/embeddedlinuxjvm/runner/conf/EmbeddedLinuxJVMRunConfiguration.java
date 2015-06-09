package com.atsebak.embeddedlinuxjvm.runner.conf;

import com.atsebak.embeddedlinuxjvm.commandline.AppCommandLineState;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunnerValidator;
import com.atsebak.embeddedlinuxjvm.ui.RunConfigurationEditor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class EmbeddedLinuxJVMRunConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {
    private EmbeddedLinuxJVMRunConfigurationRunnerParameters embeddedLinuxJVMRunConfigurationRunnerParameters = new EmbeddedLinuxJVMRunConfigurationRunnerParameters();

    /**
     * Run Configurations To Run App
     *
     * @param project
     * @param factory
     */
    protected EmbeddedLinuxJVMRunConfiguration(final Project project, final ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    /**
     * Settings Editor
     * @return
     */
    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RunConfigurationEditor(getProject());
    }

    /**
     * Creates new running paramters instance
     * @return
     */
    protected EmbeddedLinuxJVMRunConfigurationRunnerParameters createRunnerParametersInstance() {
        return new EmbeddedLinuxJVMRunConfigurationRunnerParameters();
    }


    /**
     * Read External
     * @param element
     * @throws InvalidDataException
     */
    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        embeddedLinuxJVMRunConfigurationRunnerParameters = createRunnerParametersInstance();
        XmlSerializer.deserializeInto(embeddedLinuxJVMRunConfigurationRunnerParameters, element);
    }

    /**
     * Write External
     * @param element
     * @throws WriteExternalException
     */
    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        if (embeddedLinuxJVMRunConfigurationRunnerParameters != null) {
            XmlSerializer.serializeInto(embeddedLinuxJVMRunConfigurationRunnerParameters, element);
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
        return new AppCommandLineState(env, this);
    }


    /**
     * Checks weather all the supplied paramters from the user are correct
     * @throws RuntimeConfigurationException
     */
    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        EmbeddedLinuxJVMRunnerValidator.checkJavaSettings(this);
        EmbeddedLinuxJVMRunnerValidator.checkPiSettings(getRunnerParameters());
    }

    /**
     * Gets runner paramters instance
     * @return
     */
    public EmbeddedLinuxJVMRunConfigurationRunnerParameters getRunnerParameters() {
        return embeddedLinuxJVMRunConfigurationRunnerParameters;
    }

    /**
     * Gets modules
     *
     * @return
     */
    @NotNull
    @Override
    public Module[] getModules() {
        return new Module[0];
    }
}

