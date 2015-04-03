package com.atsebak.raspberrypi.runner.conf;

import com.atsebak.raspberrypi.localization.PIBundle;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RaspberryPIConfigurationType implements ConfigurationType {
    private static final String NAME = "Raspberry PI";
    private static final String DESCRIPTION = PIBundle.getString("pi.app.description");
    private ConfigurationFactory configurationFactory;

    /**
     * Builds the configuration from the factory
     */
    public RaspberryPIConfigurationType() {
        configurationFactory = new ConfigurationFactory(this) {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new RaspberryPIRunConfiguration(project, this, NAME);
            }
        };
    }

    public static RaspberryPIConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(RaspberryPIConfigurationType.class);
    }

    /**
     * Get the name of the App
     *
     * @return
     */
    @Override
    public String getDisplayName() {
        return NAME;
    }

    /**
     * Get the Configuration Description
     *
     * @return
     */
    @Override
    public String getConfigurationTypeDescription() {
        return DESCRIPTION;
    }

    /**
     * Gets the Icon for the runner
     * @return
     */
    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/pi.png");
    }

    /**
     * Gets the ID of the Runner
     * @return
     */
    @NotNull
    @Override
    public String getId() {
        return getConfigurationTypeDescription();
    }

    /**
     * Gets the Factory
     * @return
     */
    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{configurationFactory};
    }

    public ConfigurationFactory getFactory() {
        return configurationFactory;
    }
}
