package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.ui.RaspberryPIRunConfigurationEditor;
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

public class RaspberryPIRunConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {
    private RaspberryPIRunnerParameters raspberryPIRunnerParameters = new RaspberryPIRunnerParameters();

    /**
     * Run Configurations To Run App
     *
     * @param project
     * @param factory
     */
    protected RaspberryPIRunConfiguration(final Project project, final ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    /**
     * Settings Editor
     * @return
     */
    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RaspberryPIRunConfigurationEditor(getProject());
//        SettingsEditorGroup<RaspberryPIRunConfiguration> group = new SettingsEditorGroup<RaspberryPIRunConfiguration>();
//        group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new RaspberryPIRunConfigurationEditor(getProject()));
//        JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
//        group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<RaspberryPIRunConfiguration>());
//        return group;
    }

    /**
     * Creates new running paramters instance
     * @return
     */
    protected RaspberryPIRunnerParameters createRunnerParametersInstance() {
        return new RaspberryPIRunnerParameters();
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
        return new PIAppCommandLineState(env, this);
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

    @NotNull
    @Override
    public Module[] getModules() {
        return new Module[0];
    }
}

