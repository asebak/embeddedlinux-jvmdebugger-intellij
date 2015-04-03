package com.atsebak.raspberrypi.project;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.platform.templates.BuilderBasedTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PIBuilderProjectTemplate extends BuilderBasedTemplate {

    private final String name;
    private final String description;
    private final ModuleBuilder moduleBuilder;

    public PIBuilderProjectTemplate(String name, String description, ModuleBuilder moduleBuilder) {
        super(moduleBuilder);
        this.name = name;
        this.description = description;
        this.moduleBuilder = moduleBuilder;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public String getDescription() {
        return description;
    }
}
