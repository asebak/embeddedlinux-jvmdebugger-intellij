package com.atsebak.embeddedlinuxjvm.project;

import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.atsebak.embeddedlinuxjvm.ui.EmbeddedJavaModuleStep;
import com.atsebak.embeddedlinuxjvm.utils.FileUtilities;
import com.atsebak.embeddedlinuxjvm.utils.ProjectUtils;
import com.atsebak.embeddedlinuxjvm.utils.Template;
import com.google.common.io.Files;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.PsiTestUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class RPiJavaModuleBuilder extends JavaModuleBuilder {
    private static final ProjectType PI_PROJECT_TYPE = new ProjectType("PI_JAVA");
    private static final String PROJECT_NAME = "Raspberry PI";
    private String packageName;
    @Nullable
    private File[] jarsToAdd;
    private boolean noLibrariesNeeded;

    /**
     * Used to define the hierachy of the project definition
     *
     * @param rootModel
     * @throws ConfigurationException
     */
    @Override
    public void setupRootModel(final ModifiableRootModel rootModel) throws ConfigurationException {
        super.setupRootModel(rootModel);

        final Project project = rootModel.getProject();

        final VirtualFile root = createAndGetContentEntry();
        rootModel.addContentEntry(root);

        if (myJdk != null) {
            rootModel.setSdk(myJdk);
        } else {
            rootModel.inheritSdk();
        }

        createProjectFiles(rootModel, project);

    }

    /**
     * Runs a new thread to create the required files
     *
     * @param rootModel
     * @param project
     */
    private void createProjectFiles(@NotNull final ModifiableRootModel rootModel,@NotNull final Project project) {
        ProjectUtils.runWhenInitialized(project, new DumbAwareRunnable() {
            public void run() {
                String srcPath = project.getBasePath() + File.separator + "src";
                addJarFiles(rootModel.getModule());
                String[] directoriesToMake = packageName.split(Pattern.quote("."));
                for (String directory : directoriesToMake) {
                    try {
                        VfsUtil.createDirectories(srcPath + FileUtilities.SEPARATOR + directory);
                    } catch (IOException e) {

                    }
                    srcPath += FileUtilities.SEPARATOR + directory;
                }
                Template.builder().name(getMainClassTemplateName())
                        .classContext(this.getClass())
                        .outputFile(srcPath + FileUtilities.SEPARATOR + "Main.java")
                        .data(new HashMap<String, Object>() {{
                            put("packagename", packageName);
                        }}).build()
                        .toFile();
                ProjectUtils.addProjectConfiguration(rootModel.getModule(), packageName + ".Main", getPresentableName());
            }
        });
    }

    /**
     * Creates directory for project
     * @return
     */
    private VirtualFile createAndGetContentEntry() {
        String path = FileUtil.toSystemIndependentName(getContentEntryPath());
        new File(path).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    /**
     * Returns the Module type
     * @return
     */
    @Override
    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    /**
     * Big icon is Java modules
     * @return
     */
    //@Override
    public Icon getBigIcon() {
        return IconLoader.findIcon("/pi.png");
    }

    /**
     * The icon displayed in the project creator dialog
     * @return
     */
    @Override
    public Icon getNodeIcon() {
        return IconLoader.findIcon("/pi.png");
    }

    /**
     * Build for module
     * @return
     */
    //@Override
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
     * Parent group in project creator dialog
     * @return
     */
    @Override
    public String getParentGroup() {
        return JavaModuleType.BUILD_TOOLS_GROUP;
    }

    /**
     * get weight
     * @return
     */
    @Override
    public int getWeight() {
        return JavaModuleBuilder.BUILD_SYSTEM_WEIGHT;
    }

    /**
     * Help description of the module
     * @return
     */
    @Override
    public String getDescription() {
        return EmbeddedLinuxJVMBundle.getString("pi.project.description");
    }

    /**
     * Setup module with PI4J
     *
     * @param module
     * @throws ConfigurationException
     */
    @SneakyThrows(IOException.class)
    @Override
    protected void setupModule(Module module) throws ConfigurationException {
        super.setupModule(module);
        if(!noLibrariesNeeded) {
            final String libPath = module.getProject().getBasePath() + File.separator + "lib";
            VfsUtil.createDirectories(libPath);
            File outputFiles = new File(libPath);
            FileUtilities.unzip(getClass().getResourceAsStream("/pi4j.zip"), outputFiles.getAbsolutePath());
            jarsToAdd = outputFiles.listFiles();
        }
    }

    /**
     * Project Type
     * @return
     */
    @Override
    protected ProjectType getProjectType() {
        return PI_PROJECT_TYPE;
    }

    private void addJarFiles(Module module) {
        if (jarsToAdd == null) {
            return;
        }

        for (final File fileEntry : jarsToAdd) {
            if (!fileEntry.isDirectory() && Files.getFileExtension(fileEntry.getName()).contains("jar")) {
                PsiTestUtil.addLibrary(module, fileEntry.getName(), fileEntry.getParentFile().getPath(), fileEntry.getName());
            }
        }
    }

    /**
     * Adds a custom wizard GUI
     * @param context
     * @param parentDisposable
     * @return
     */
    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        EmbeddedJavaModuleStep step = new EmbeddedJavaModuleStep(this);
        Disposer.register(parentDisposable, step);
        return step;
    }

    /**
     * Not Needed
     * @param list
     */
    @Override
    public void setSourcePaths(List<Pair<String, String>> list) {

    }

    /**
     * Not Needed
     * @param pair
     */
    @Override
    public void addSourcePath(Pair<String, String> pair) {

    }

    /**
     * gets file marker file name
     * @return
     */
    public String getMainClassTemplateName() {
        return "rpimain.ftl";
    }


}
