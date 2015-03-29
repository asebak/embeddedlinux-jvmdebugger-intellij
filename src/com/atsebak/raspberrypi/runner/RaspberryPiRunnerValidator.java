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
            throw new RuntimeConfigurationException(PIBundle.getString("pi.invalid.xdisplay"));
        }
        if (rp.getHostname() == null || rp.getHostname().isEmpty()) {
            throw new RuntimeConfigurationException(PIBundle.getString("pi.invalid.hostname"));
        }
        if (rp.getPort() == null || rp.getPort().isEmpty()) {
            throw new RuntimeConfigurationException(PIBundle.getString("pi.invalid.port"));
        }
        if (rp.getUsername() == null || rp.getUsername().isEmpty()) {
            throw new RuntimeConfigurationException(PIBundle.getString("pi.invalid.username"));
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
        JavaRunConfigurationModule var1 = configuration.getConfigurationModule();
        PsiClass var2 = var1.checkModuleAndClassName(configuration.MAIN_CLASS_NAME, ExecutionBundle.message("no.main.class.specified.error.text"));
        if (!PsiMethodUtil.hasMainMethod(var2)) {
            throw new RuntimeConfigurationWarning(ExecutionBundle.message("main.method.not.found.in.class.error.message", configuration.MAIN_CLASS_NAME));
        } else {
            ProgramParametersUtil.checkWorkingDirectoryExist(configuration, configuration.getProject(), var1.getModule());
            JavaRunConfigurationExtensionManager.checkConfigurationIsValid(configuration);
        }
    }
}
