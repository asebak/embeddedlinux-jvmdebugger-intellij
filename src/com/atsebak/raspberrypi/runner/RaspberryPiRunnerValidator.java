package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.localization.PIBundle;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.JavaRunConfigurationExtensionManager;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.configurations.RuntimeConfigurationWarning;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiMethodUtil;


public class RaspberryPiRunnerValidator {
    /**
     * Validates the PI Settings are Entered Correctly
     *
     * @param rp
     * @throws RuntimeConfigurationException
     */
    public static void checkPiSettings(RaspberryPIRunnerParameters rp) throws RuntimeConfigurationException {
        if (rp.getDisplay() == null || rp.getDisplay().isEmpty()) {
            throw new RuntimeConfigurationWarning(PIBundle.getString("pi.invalid.xdisplay"));
        }
        if (rp.getHostname() == null || rp.getHostname().isEmpty()) {
            throw new RuntimeConfigurationWarning(PIBundle.getString("pi.invalid.hostname"));
        }
        if (rp.getPort() == null || rp.getPort().isEmpty()) {
            throw new RuntimeConfigurationWarning(PIBundle.getString("pi.invalid.port"));
        }
        if (rp.getUsername() == null || rp.getUsername().isEmpty()) {
            throw new RuntimeConfigurationWarning(PIBundle.getString("pi.invalid.username"));
        }
    }

    /**
     * Validates if the Java Settings are Entered Correctly
     *
     * @param configuration
     * @throws RuntimeConfigurationException
     */
    public static void checkJavaSettings(RaspberryPIRunConfiguration configuration) throws RuntimeConfigurationException {
        JavaParametersUtil.checkAlternativeJRE(configuration);
        JavaRunConfigurationModule javaRunConfigurationModule = configuration.getConfigurationModule();
        PsiClass psiClass = javaRunConfigurationModule.checkModuleAndClassName(configuration.getRunClass(), ExecutionBundle.message("no.main.class.specified.error.text"));
        if (!PsiMethodUtil.hasMainMethod(psiClass)) {
            throw new RuntimeConfigurationWarning(ExecutionBundle.message("main.method.not.found.in.class.error.message", configuration.getRunClass()));
        } else {
            ProgramParametersUtil.checkWorkingDirectoryExist(configuration, configuration.getProject(), javaRunConfigurationModule.getModule());
            JavaRunConfigurationExtensionManager.checkConfigurationIsValid(configuration);
        }
    }
}
