package com.atsebak.raspberrypi.project;


import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
import com.intellij.platform.templates.BuilderBasedTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PIProjectTemplateFactory extends ProjectTemplatesFactory {
    public static final String PI = "Raspberry PI";
    public static final String MAVEN_MODULE = "Maven";

    public static final ProjectTemplate[] EMPTY_PROJECT_TEMPLATES = new ProjectTemplate[]{};

    @NotNull
    @Override
    public String[] getGroups() {
        return new String[]{PI};
    }

    @Override
    public Icon getGroupIcon(String group) {
        return IconLoader.getIcon("/pi.png");
    }

    @Override
    public String getParentGroup(String group) {
        return "Java";
    }

    @NotNull
    @Override
    public ProjectTemplate[] createTemplates(@Nullable String group, WizardContext context) {
        Project project = context.getProject();
        if (project != null) {
            return EMPTY_PROJECT_TEMPLATES;
        }
        ProjectTemplate[] templates = {
                new BuilderBasedTemplate(new PIJavaModuleBuilder()),
                new PIBuilderProjectTemplate(MAVEN_MODULE,
                        "Raspberry PI and Maven for dependencies management.", new PIMavenModuleBuilder())
        };
        return templates;
    }
}