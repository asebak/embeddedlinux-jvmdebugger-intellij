package com.atsebak.raspberrypi.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public class RaspberryPIConfigurationType implements ConfigurationType {
    private ConfigurationFactory configurationFactory;
    private static final String NAME = "Raspberry PI";
    private static final String DESCRIPTION = "Run as a Raspberry PI Application";
    public RaspberryPIConfigurationType() {
        configurationFactory = new ConfigurationFactory(this) {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new RaspberryPIRunConfiguration(project, this, NAME);
            }
        };
    }
    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public String getConfigurationTypeDescription() {
        return DESCRIPTION;
    }

    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/pi.png");
    }

    @NotNull
    @Override
    public String getId() {
        return getConfigurationTypeDescription();
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{configurationFactory};
    }
}
