package com.atsebak.raspberrypi.project;

import com.google.common.io.Files;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.testFramework.PsiTestUtil;
import lombok.Setter;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Setter
public class PIJavaModuleBuilder extends JavaModuleBuilder {
    public static final ProjectType PI_PROJECT_TYPE = new ProjectType("PI_JAVA");
    private static final String PI4J_DOWNLOAD = "http://get.pi4j.com/download/";
    private static final String PI4J_FILENAME = "pi4j-1.1-SNAPSHOT.zip";
    private static final String PI4J_INSTALLPATH = "/opt/pi4j/lib";
    private String packageName;
    private String mainName;

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        super.setupRootModel(rootModel);
        final Project project = rootModel.getProject();
//        final VirtualFile root = createAndGetContentEntry();
//        rootModel.addContentEntry(root);
        if (myJdk != null) {
            rootModel.setSdk(myJdk);
        } else {
            rootModel.inheritSdk();
        }
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
                    saveUrl(System.getProperty("java.io.tmpdir") + File.separator + PI4J_FILENAME, PI4J_DOWNLOAD + PI4J_FILENAME);
                }
                //unzip to temp
                String output = System.getProperty("java.io.tmpdir") + File.separator + "pi4j";
                File pi4jUnziped = new File(output);
                if (!pi4jUnziped.exists()) {
                    unZipIt(pi4jZip.getPath(), output);
                }
                addJarFiles(module, pi4jUnziped.listFiles());
            } catch (IOException e) {
            }
        }
    }

    @Override
    protected ProjectType getProjectType() {
        return PI_PROJECT_TYPE;
    }

    /**
     * Saves a file to a path
     *
     * @param filename
     * @param urlString
     * @throws IOException
     */
    private void saveUrl(final String filename, final String urlString) throws IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (Exception e) {
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    /**
     * Unzips a file all into one directory
     *
     * @param zipFile
     * @param outputFolder
     */
    public void unZipIt(String zipFile, String outputFolder) {
        byte[] buffer = new byte[1024];
        try {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                if (ze.isDirectory()) {
                    ze = zis.getNextEntry();
                    continue;
                }
                fileName = new File(fileName).getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException ex) {
        }
    }

    private void addJarFiles(Module module, File[] files) {
        for (final File fileEntry : files) {
            if (!fileEntry.isDirectory() && Files.getFileExtension(fileEntry.getName()).contains("jar")) {
                PsiTestUtil.addLibrary(module, "pi4j", fileEntry.getParentFile().getPath(), fileEntry.getName());
            }
        }
    }

}
