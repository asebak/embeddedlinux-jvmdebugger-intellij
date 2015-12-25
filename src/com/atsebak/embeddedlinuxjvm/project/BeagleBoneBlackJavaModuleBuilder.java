package com.atsebak.embeddedlinuxjvm.project;

import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.intellij.openapi.project.ProjectType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class BeagleBoneBlackJavaModuleBuilder extends RPiJavaModuleBuilder {
    private static final ProjectType BBB_PROJECT_TYPE = new ProjectType("BBB_JAVA");
    private static final String PROJECT_NAME = "BeagleBone Black";


    /**
     * The icon displayed in the project creator dialog
     * @return
     */
    @Override
    public Icon getNodeIcon() {
        return IconLoader.findIcon("/bbb.png");
    }

    /**
     * Build for module
     * @return
     */
    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    /**
     * Module name
     * @return
     */
    @Override
    public String getPresentableName() {
        return PROJECT_NAME;
    }


    /**
     * Help description of the module
     * @return
     */
    @Override
    public String getDescription() {
        return EmbeddedLinuxJVMBundle.getString("bbb.project.description");
    }


    /**
     * Project Type
     * @return
     */
    @Override
    protected ProjectType getProjectType() {
        return BBB_PROJECT_TYPE;
    }

    /**
     * gets file marker file name
     * @return
     */
    public String getMainClassTemplateName() {
        return "bbbmain.ftl";
    }

    @Nullable
    @Override
    public File[] getJarsToAdd() {
        return null;
    }

    @Override
    public void setNoLibrariesNeeded(boolean noLibrariesNeeded) {
        super.setNoLibrariesNeeded(true);
    }
}
