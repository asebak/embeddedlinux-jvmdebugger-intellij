package com.blocklatency.embeddedlinuxjvm.utils;

import com.blocklatency.embeddedlinuxjvm.hal.LinuxHAL;
import com.blocklatency.embeddedlinuxjvm.hal.WindowsHAL;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.SimpleJavaParameters;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.JdkUtil;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.util.Computable;

public class RemoteCommandLineBuilder {
    public static GeneralCommandLine createFromJavaParameters(final SimpleJavaParameters javaParameters,
                                                              final Project project,
                                                              final boolean dynamicClasspath) throws CantRunException {
        return createFromJavaParameters(javaParameters, dynamicClasspath && JdkUtil.useDynamicClasspath(project));
    }

    /**
     * @param javaParameters        parameters.
     * @param forceDynamicClasspath whether dynamic classpath will be used for this execution, to prevent problems caused by too long command line.
     * @return a command line.
     * @throws CantRunException if there are problems with JDK setup.
     */
    public static GeneralCommandLine createFromJavaParameters(final SimpleJavaParameters javaParameters,
                                                              final boolean forceDynamicClasspath) throws CantRunException {
        try {
            return ApplicationManager.getApplication().runReadAction(new Computable<GeneralCommandLine>() {
                public GeneralCommandLine compute() {
                    try {
                        final Sdk jdk = javaParameters.getJdk();
                        if (jdk == null) {
                            throw new CantRunException(ExecutionBundle.message("run.configuration.error.no.jdk.specified"));
                        }

                        final SdkTypeId sdkType = jdk.getSdkType();
                        if (!(sdkType instanceof JavaSdkType)) {
                            throw new CantRunException(ExecutionBundle.message("run.configuration.error.no.jdk.specified"));
                        }

                        final String exePath = ((JavaSdkType)sdkType).getVMExecutablePath(jdk);
                        if (exePath == null) {
                            throw new CantRunException(ExecutionBundle.message("run.configuration.cannot.find.vm.executable"));
                        }
                        if (javaParameters.getMainClass() == null && javaParameters.getJarPath() == null) {
                            throw new CantRunException(ExecutionBundle.message("main.class.is.not.specified.error.message"));
                        }
                        return RemoteJdk.setupJVMCommandLine(OSUtils.isWindows() ? new WindowsHAL() : new LinuxHAL());
                    }
                    catch (CantRunException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        catch (RuntimeException e) {
            if (e.getCause() instanceof CantRunException) {
                throw (CantRunException)e.getCause();
            }
            else {
                throw e;
            }
        }
    }

}
