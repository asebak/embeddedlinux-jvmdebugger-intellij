package com.atsebak.raspberrypi.project;

import com.atsebak.raspberrypi.ui.PIJavaModuleStep;
import com.atsebak.raspberrypi.utils.FileZip;
import com.atsebak.raspberrypi.utils.ProjectUtils;
import com.atsebak.raspberrypi.utils.UrlDownloader;
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
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Setter
@NoArgsConstructor
public class PIJavaModuleBuilder extends JavaModuleBuilder {
    public static final ProjectType PI_PROJECT_TYPE = new ProjectType("PI_JAVA");
    private static final String PROJECT_NAME = "Raspberry PI";
    private static final String PI4J_DOWNLOAD = "http://get.pi4j.com/download/";
    private static final String PI4J_FILENAME = "pi4j-1.1-SNAPSHOT.zip";
    private static final String PI4J_INSTALLPATH = "/opt/pi4j/lib";
    private String packageName;

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

        ProjectUtils.runWhenInitialized(project, new DumbAwareRunnable() {
            public void run() {
                String[] directorysToMake = packageName.split(Pattern.quote("."));
                String basePath = project.getBasePath() + "/src";
                for (String directory : directorysToMake) {
                    try {
                        VfsUtil.createDirectories(basePath + File.separator + directory);
                        basePath += File.separator + directory;
                    } catch (IOException e) {

                    }
                }
                Configuration configuration = new Configuration();
                configuration.setClassForTemplateLoading(this.getClass(), "/");
                try {
                    Template template = configuration.getTemplate("main.ftl");
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("packagename", packageName);
                    Writer file = new FileWriter(new File(basePath + File.separator + "Main.java"));
                    template.process(data, file);
                    file.flush();
                    file.close();

                } catch (Exception e) {

                }

                ProjectUtils.addProjectConfiguration(rootModel.getModule(), project, packageName + ".Main");
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
    @Override
    public Icon getBigIcon() {
        return AllIcons.Modules.Types.JavaModule;
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
        return "A basic java application project that uses <b>PI4J/b> library as the backbone.";
    }

    /**
     * Setup module with PI4J
     *
     * @param module
     * @throws ConfigurationException
     */
    @Override
    protected void setupModule(Module module) throws ConfigurationException {
        super.setupModule(module);
        File pi4j = new File(PI4J_INSTALLPATH);

        //user installed library already
        if (pi4j.exists()) {
            addJarFiles(module, pi4j.listFiles());
        } else {
            try {
                //download library
                File pi4jZip = new File(System.getProperty("java.io.tmpdir") + File.separator + PI4J_FILENAME);
                if (!pi4jZip.exists()) {
                    UrlDownloader.saveUrl(System.getProperty("java.io.tmpdir") + File.separator + PI4J_FILENAME, PI4J_DOWNLOAD + PI4J_FILENAME);
                }
                //unzip to temp
                String output = System.getProperty("java.io.tmpdir") + File.separator + "pi4j";
                File pi4jUnziped = new File(output);
                if (!pi4jUnziped.exists()) {
                    FileZip.unzip(pi4jZip.getPath(), output);
                }
                addJarFiles(module, pi4jUnziped.listFiles());
            } catch (IOException e) {
            }
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

    private void addJarFiles(Module module, File[] files) {
        for (final File fileEntry : files) {
            if (!fileEntry.isDirectory() && Files.getFileExtension(fileEntry.getName()).contains("jar")) {
                PsiTestUtil.addLibrary(module, "pi4j", fileEntry.getParentFile().getPath(), fileEntry.getName());
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
        PIJavaModuleStep step = new PIJavaModuleStep(this);
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
}
