package com.blocklatency.embeddedlinuxjvm.utils;


import com.intellij.openapi.project.Project;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtilities {

    public static final String LIB = "lib";
    public static final String CLASSES = "classes";
    public static final String SEPARATOR = "/";

    /**
     * Unzips a file all into one directory
     *
     * @param inputStream
     * @param outputFolder
     */
    public static void unzip(InputStream inputStream, String outputFolder) {
        byte[] buffer = new byte[1024];
        try {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            ZipInputStream zis = new ZipInputStream(inputStream);
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                if (ze.isDirectory()) {
                    ze = zis.getNextEntry();
                    continue;
                }
                fileName = new File(fileName).getName();
                File newFile = new File(outputFolder + SEPARATOR + fileName);
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


    /**
     * Creates the file that will be uploaded to the target
     *
     * @param classpathEntries
     * @param project
     * @return
     * @throws IOException
     */
    public static File createClasspathArchive(Collection<File> classpathEntries, Project project) throws IOException {
        File archiveFile = new File(System.getProperty("java.io.tmpdir"), project.getName() + ".tar");
        if (archiveFile.exists()) {
            archiveFile.delete();
        }

        FileOutputStream fileOutputStream = null;
        TarArchiveOutputStream archiveOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(archiveFile);
            archiveOutputStream = new TarArchiveOutputStream(new GZIPOutputStream(fileOutputStream));
            archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            LinkedList<String> pathElements = new LinkedList<String>();
            for (File f : classpathEntries) {
                if (f.isFile()) { //is a jar file
                    pathElements.addLast(LIB);
                    writeClassPath(pathElements, f, archiveOutputStream);
                } else {
                    pathElements.addLast(CLASSES);  // is output of the project
                    for (File child : f.listFiles()) {
                        writeClassPath(pathElements, child, archiveOutputStream);
                    }
                }
                pathElements.removeLast();
            }
            return archiveFile;
        } finally {
            if (archiveOutputStream != null) {
                archiveOutputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    /**
     * @param pathElements
     * @param entry
     * @param archiveOutputStream
     * @throws IOException
     */
    private static void writeClassPath(LinkedList<String> pathElements, File entry, TarArchiveOutputStream archiveOutputStream) throws IOException {
        if (entry.isFile()) {
            archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            archiveOutputStream.putArchiveEntry(new TarArchiveEntry(entry, getPath(pathElements) + SEPARATOR + entry.getName()));
            copy(entry, archiveOutputStream);
            archiveOutputStream.closeArchiveEntry();
        } else {
            pathElements.addLast(entry.getName());
            for (File child : entry.listFiles()) {
                writeClassPath(pathElements, child, archiveOutputStream);
            }
            pathElements.removeLast();
        }
    }

    /**
     * Copys file
     *
     * @param entry
     * @param out
     * @throws IOException
     */
    public static void copy(File entry, OutputStream out) throws IOException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(entry);
            IOUtils.copy(fileInputStream, out);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    /**
     * Get Path
     *
     * @param pathElements
     * @return
     */
    public static String getPath(LinkedList<String> pathElements) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < pathElements.size(); i++) {
            if (i != 0) {
                buf.append(SEPARATOR);
            }
            buf.append(pathElements.get(i));
        }
        return buf.toString();
    }

}
