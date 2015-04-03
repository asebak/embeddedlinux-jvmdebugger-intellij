package com.atsebak.raspberrypi.utils;


import com.intellij.openapi.project.Project;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtilities {

    /**
     * Unzips a file all into one directory
     *
     * @param zipFile
     * @param outputFolder
     */
    public static void unzip(String zipFile, String outputFolder) {
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
        ArchiveOutputStream archiveOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(archiveFile);
            archiveOutputStream = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.TAR, fileOutputStream);
            LinkedList<String> pathElements = new LinkedList<String>();
            for (File f : classpathEntries) {
                if (f.isFile()) { //is a jar file
                    pathElements.addLast("lib");
                    writeClassPath(pathElements, f, archiveOutputStream);
                } else {
                    pathElements.addLast("classes");  // is output of the project
                    for (File child : f.listFiles()) {
                        writeClassPath(pathElements, child, archiveOutputStream);
                    }
                }
                pathElements.removeLast();
            }
            return archiveFile;
        } catch (ArchiveException e) {
            throw new IOException("Failed to create classpath archive", e);
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
     * @param os
     * @throws IOException
     */
    private static void writeClassPath(LinkedList<String> pathElements, File entry, ArchiveOutputStream os) throws IOException {
        if (entry.isFile()) {
            os.putArchiveEntry(new TarArchiveEntry(entry, getPath(pathElements) + File.separator + entry.getName()));
            copy(entry, os);
            os.closeArchiveEntry();
        } else {
            pathElements.addLast(entry.getName());
            for (File child : entry.listFiles()) {
                writeClassPath(pathElements, child, os);
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
        FileInputStream in = null;
        try {
            in = new FileInputStream(entry);
            IOUtils.copy(in, out);
        } finally {
            if (in != null) {
                in.close();
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
                buf.append(File.separator);
            }
            buf.append(pathElements.get(i));
        }
        return buf.toString();
    }
}
