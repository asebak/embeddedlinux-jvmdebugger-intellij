package com.atsebak.embeddedlinuxjvm.runner.data;

import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.configurations.RuntimeConfigurationWarning;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;


public class EmbeddedLinuxJVMRunnerValidator {
    /**
     * Validates the PI Settings are Entered Correctly
     *
     * @param rp
     * @throws RuntimeConfigurationException
     */
    public static void checkPiSettings(EmbeddedLinuxJVMRunConfigurationRunnerParameters rp) throws RuntimeConfigurationWarning {
        if (StringUtil.isEmptyOrSpaces(rp.getHostname())) {
            throw new RuntimeConfigurationWarning(EmbeddedLinuxJVMBundle.getString("pi.invalid.hostname"));
        }
        if (StringUtil.isEmptyOrSpaces(rp.getPort())) {
            throw new RuntimeConfigurationWarning(EmbeddedLinuxJVMBundle.getString("pi.invalid.port"));
        }
        if (StringUtil.isEmptyOrSpaces(rp.getUsername())) {
            throw new RuntimeConfigurationWarning(EmbeddedLinuxJVMBundle.getString("pi.invalid.username"));
        }
    }

    /**
     * Validates if the Java Settings are Entered Correctly
     *
     * @param configuration
     * @throws RuntimeConfigurationException
     */
    public static void checkJavaSettings(EmbeddedLinuxJVMRunConfiguration configuration) throws RuntimeConfigurationException {
        JavaRunConfigurationModule javaRunConfigurationModule = new JavaRunConfigurationModule(configuration.getProject(), false);
        PsiClass psiClass = javaRunConfigurationModule.findClass(configuration.getRunnerParameters().getMainclass());
        if (psiClass == null) {
            throw new RuntimeConfigurationWarning(ExecutionBundle.message("main.method.not.found.in.class.error.message", configuration.getRunnerParameters().getMainclass()));
        }
    }
}
