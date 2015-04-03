package com.atsebak.raspberrypi.runner.data;

import com.atsebak.raspberrypi.localization.PIBundle;
import com.atsebak.raspberrypi.runner.conf.RaspberryPIRunConfiguration;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.configurations.RuntimeConfigurationWarning;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;


public class RaspberryPiRunnerValidator {
    /**
     * Validates the PI Settings are Entered Correctly
     *
     * @param rp
     * @throws RuntimeConfigurationException
     */
    public static void checkPiSettings(RaspberryPIRunnerParameters rp) throws RuntimeConfigurationWarning {
        if (!isValidHost(rp.getHostname())) {
            throw new RuntimeConfigurationWarning(PIBundle.getString("pi.invalid.hostname"));
        }
        if (StringUtil.isEmptyOrSpaces(rp.getPort())) {
            throw new RuntimeConfigurationWarning(PIBundle.getString("pi.invalid.port"));
        }
        if (StringUtil.isEmptyOrSpaces(rp.getUsername())) {
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
        JavaRunConfigurationModule javaRunConfigurationModule = new JavaRunConfigurationModule(configuration.getProject(), false);
        PsiClass psiClass = javaRunConfigurationModule.findClass(configuration.getRunnerParameters().getMainclass());
        if (psiClass == null) {
            throw new RuntimeConfigurationWarning(ExecutionBundle.message("main.method.not.found.in.class.error.message", configuration.getRunnerParameters().getMainclass()));
        }
    }

    /**
     * Validates host can be contacted
     *
     * @param ip
     * @return
     * @throws RuntimeConfigurationWarning
     */
    private static boolean isValidHost(String ip) throws RuntimeConfigurationWarning {
        return !(StringUtil.isEmptyOrSpaces(ip));
        //todo validate if real ipv4/v6 address
    }
}
