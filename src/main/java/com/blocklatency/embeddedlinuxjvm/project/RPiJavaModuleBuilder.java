package com.blocklatency.embeddedlinuxjvm.project;

import com.blocklatency.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.blocklatency.embeddedlinuxjvm.ui.EmbeddedJavaModuleStep;
import com.blocklatency.embeddedlinuxjvm.utils.FileUtilities;
import com.blocklatency.embeddedlinuxjvm.utils.ProjectUtils;
import com.blocklatency.embeddedlinuxjvm.utils.Template;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectType;
import com.intellij.openapi.roots.JavadocOrderRootType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.FileNameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
    private boolean noLibrariesNeeded = true;

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
            FileUtilities.unzip(Objects.requireNonNull(getClass().getResource("/pi4j-2.1.1.zip")).openStream(), outputFiles.getAbsolutePath());
            jarsToAdd = Arrays.stream(Objects.requireNonNull(outputFiles.listFiles())).filter(x -> FileNameUtils.getExtension(x.getName()).contains("jar")).toArray(File[]::new);
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
        //referenced: https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000036724-Programmatically-add-directory-based-dependency-library-to-module
        attachDirBasedLibrary(module, "pi4j", Arrays.stream(jarsToAdd).findFirst().get().getParentFile().getPath());
    }

    private void attachDirBasedLibrary(@NotNull final Module module,
                                       @NotNull final String libName,
                                       @NotNull final String dir)
    {
        ApplicationManager.getApplication().invokeLater(() ->
        {
            final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
            final String urlString = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, dir);
            final VirtualFile dirVirtualFile = VirtualFileManager.getInstance().findFileByUrl(urlString);
            if (dirVirtualFile != null)
            {
                final ModifiableRootModel modifiableModel = rootManager.getModifiableModel();
                final Library newLib = createDirLib(libName, dirVirtualFile, modifiableModel.getModuleLibraryTable());
                modifiableModel.commit();
            }
        });
    }


    private Library createDirLib(@NotNull final String libName,
                                 @NotNull final VirtualFile dirVirtualFile,
                                 @NotNull final LibraryTable table)
    {
        Library library = table.getLibraryByName(libName);
        if (library == null)
        {
            library = table.createLibrary(libName);

            Library.ModifiableModel libraryModel = library.getModifiableModel();
            libraryModel.addJarDirectory(dirVirtualFile,  true, OrderRootType.CLASSES);
            libraryModel.addJarDirectory(dirVirtualFile,  true, OrderRootType.SOURCES);
            libraryModel.addJarDirectory(dirVirtualFile, true, JavadocOrderRootType.getInstance());
            libraryModel.commit();

        }
        return library;
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
