package com.atsebak.raspberrypi.runner;

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.JavaRunConfigurationExtensionManager;
import com.intellij.execution.application.ApplicationConfigurable;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configuration.EmptyRunProfileState;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

//ApplicationConfiguration RunConfigurationBase

public class RaspberryPIRunConfiguration extends ApplicationConfiguration {
    private RaspberryPIRunnerParameters raspberryPIRunnerParameters = new RaspberryPIRunnerParameters();
    private Project project;
    protected RaspberryPIRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(name, project, factory);
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
        return EmptyRunProfileState.INSTANCE;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        //todo validate configuration here
//        checkURL(raspberryPIRunnerParameters.getHostname());
    }
    public RaspberryPIRunnerParameters getRunnerParameters() {
        return raspberryPIRunnerParameters;
    }
}
