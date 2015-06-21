package com.atsebak.embeddedlinuxjvm.services;

import com.intellij.openapi.project.Project;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClasspathServiceTest {
    private final Project project = mock(Project.class);
    private ClasspathService classpathService;
    private File jar1 = mock(File.class);
    private File jar2 = mock(File.class);
    private File output = mock(File.class);
    private List<File> hostFiles;

    @Before
    public void setUp() throws Exception {
        classpathService = new ClasspathService(project);
        hostFiles = Arrays.asList(jar1, jar2, output);
        when(jar1.getName()).thenReturn("randomjar1.jar");
        when(jar2.getName()).thenReturn("randomjar2.jar");
        when(output.getName()).thenReturn("untitledproject");
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
}