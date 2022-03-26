package com.atsebak.embeddedlinuxjvm.runner.conf;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EmbeddedLinuxJVMConfigurationType implements ConfigurationType {
    private static final String NAME = "Embedded Linux JVM";
    private static final String DESCRIPTION = "Run as an Embedded Java application";
    private ConfigurationFactory configurationFactory;

    /**
     * Builds the configuration from the factory
     */
    public EmbeddedLinuxJVMConfigurationType() {
        configurationFactory = new ConfigurationFactory(this) {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new EmbeddedLinuxJVMRunConfiguration(project, this, NAME);
            }

            @Override
            public @NotNull
            @NonNls String getId() {
                return DESCRIPTION;
            }
        };
    }

    public static EmbeddedLinuxJVMConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(EmbeddedLinuxJVMConfigurationType.class);
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
        return DESCRIPTION;
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
