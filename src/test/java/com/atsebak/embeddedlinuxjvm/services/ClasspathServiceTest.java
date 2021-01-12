package com.atsebak.embeddedlinuxjvm.services;

import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSHHandlerTarget;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class ClasspathServiceTest {
    private static final long JAR1_LAST_MODIFIED = 12345678;
    private static final String JAR1_NAME = "randomjar1.jar";
    private static final long JAR1_SIZE = 1000;
    private static final long JAR2_LAST_MODIFIED = 11111111;
    private static final String JAR2_NAME = "randomjar2.jar";
    private static final long JAR2_SIZE = 3000;
    private final Project project = mock(Project.class);
    private final SSHHandlerTarget target = mock(SSHHandlerTarget.class);
    private ClasspathService classpathService;
    private File jar1 = mock(File.class);
    private File jar2 = mock(File.class);
    private ChannelSftp.LsEntry jar1Server = mock(ChannelSftp.LsEntry.class);
    private ChannelSftp.LsEntry jar2Server = mock(ChannelSftp.LsEntry.class);
    private File output = mock(File.class);
    private List<File> hostFiles;

    @Before
    public void setUp() throws Exception {
        classpathService = new ClasspathService(project);
        hostFiles = Arrays.asList(jar1, jar2, output);
        when(jar1.getName()).thenReturn(JAR1_NAME);
        when(jar1.lastModified()).thenReturn(JAR1_LAST_MODIFIED);
        when(jar1.length()).thenReturn(JAR1_SIZE);
        when(jar2.getName()).thenReturn(JAR2_NAME);
        when(jar2.length()).thenReturn(JAR2_SIZE);
        when(jar2.lastModified()).thenReturn(JAR2_LAST_MODIFIED);
        when(output.getName()).thenReturn("untitledproject");
        when(jar1Server.getFilename()).thenReturn(JAR1_NAME);
        when(jar2Server.getFilename()).thenReturn(JAR2_NAME);
        when(jar1Server.getAttrs()).thenReturn(mock(SftpATTRS.class));
        when(jar2Server.getAttrs()).thenReturn(mock(SftpATTRS.class));
        Vector vector = new Vector();
        vector.add(jar1Server);
        vector.add(jar2Server);
        when(target.getAlreadyDeployedLibraries()).thenReturn(vector);
    }

    @Test
    public void testFirstDeployment() {
        List<File> filesToDeploy = classpathService.deltaOfDeployedJars(hostFiles);
        assertEquals(filesToDeploy, hostFiles);
    }

    @Test
    public void testSecondDeployment() {
        List<File> filesToDeploy = classpathService.deltaOfDeployedJars(hostFiles);
        assertEquals(filesToDeploy, hostFiles);
        filesToDeploy = classpathService.deltaOfDeployedJars(hostFiles);
        assertEquals(filesToDeploy, Arrays.asList(output));
    }

    @Test
    public void givenSameNameSizeAndDateOnlyOutputShouldBePresent() throws JSchException, RuntimeConfigurationException, SftpException, IOException {
        when(jar1Server.getAttrs().getSize()).thenReturn(JAR1_SIZE);
        when(jar2Server.getAttrs().getSize()).thenReturn(JAR2_SIZE);
        when(jar1Server.getAttrs().getMtimeString()).thenReturn(new Date(JAR1_LAST_MODIFIED).toString());
        when(jar2Server.getAttrs().getMtimeString()).thenReturn(new Date(JAR2_LAST_MODIFIED).toString());
        List<File> files = classpathService.invokeFindDeployedJars(hostFiles, target);
        assertEquals(files.size(), 1);
        assertEquals(files.get(0).getName(), output.getName());
    }

    @Test
    public void givenSameNameSizeDifferentDateOutputShouldNotBePresent() throws SftpException, RuntimeConfigurationException, JSchException, IOException {
        when(jar1Server.getAttrs().getSize()).thenReturn(JAR1_SIZE * 10);
        when(jar2Server.getAttrs().getSize()).thenReturn(JAR2_SIZE * 10);
        when(jar1Server.getAttrs().getMtimeString()).thenReturn(new Date(JAR1_LAST_MODIFIED).toString());
        when(jar2Server.getAttrs().getMtimeString()).thenReturn(new Date(JAR2_LAST_MODIFIED).toString());
        List<File> files = classpathService.invokeFindDeployedJars(hostFiles, target);
        assertEquals(files.size(), hostFiles.size());
    }

    @Test
    public void givenSameNameDateDifferentSizeOutputShouldNotBePresent() throws SftpException, RuntimeConfigurationException, JSchException, IOException {
        when(jar1Server.getAttrs().getSize()).thenReturn(JAR1_SIZE);
        when(jar2Server.getAttrs().getSize()).thenReturn(JAR2_SIZE);
        when(jar1Server.getAttrs().getMtimeString()).thenReturn(new Date(JAR1_LAST_MODIFIED * 2).toString());
        when(jar2Server.getAttrs().getMtimeString()).thenReturn(new Date(JAR2_LAST_MODIFIED * 2).toString());
        List<File> files = classpathService.invokeFindDeployedJars(hostFiles, target);
        assertEquals(files.size(), hostFiles.size());
    }

    @Test
    public void givenSameNameDateDifferentSizeJustForOneOutputShouldNotBePresent() throws SftpException, RuntimeConfigurationException, JSchException, IOException {
        when(jar1Server.getAttrs().getSize()).thenReturn(JAR1_SIZE);
        when(jar2Server.getAttrs().getSize()).thenReturn(JAR2_SIZE);
        when(jar1Server.getAttrs().getMtimeString()).thenReturn(new Date(JAR1_LAST_MODIFIED).toString());
        when(jar2Server.getAttrs().getMtimeString()).thenReturn(new Date(JAR2_LAST_MODIFIED * 2).toString());
        List<File> files = classpathService.invokeFindDeployedJars(hostFiles, target);
        assertEquals(files.size(), hostFiles.size() - 1);
    }
}