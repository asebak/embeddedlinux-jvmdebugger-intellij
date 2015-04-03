package com.atsebak.raspberrypi.ui;

import com.atsebak.raspberrypi.project.PIJavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.lang.java.lexer.JavaLexer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.pom.java.LanguageLevel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PIJavaModuleStep extends ModuleWizardStep implements Disposable {
    private PIJavaModuleBuilder piJavaModuleBuilder;
    private JPanel main;
    private JTextField packageField;

    public PIJavaModuleStep(@NotNull PIJavaModuleBuilder piJavaModuleBuilder) {
        this.piJavaModuleBuilder = piJavaModuleBuilder;
    }

    private static boolean isValidFullyQualifiedJavaIdentifier(String value) {
        return isValidJavaPackageName(value) && value.indexOf('.') != -1;
    }

    /**
     * Checks if the given name is a valid general Java package name.
     * <p/>
     */
    public static boolean isValidJavaPackageName(@NotNull String name) {
        int index = 0;
        while (true) {
            int index1 = name.indexOf('.', index);
            if (index1 < 0) index1 = name.length();
            if (!isIdentifier(name.substring(index, index1))) return false;
            if (index1 == name.length()) return true;
            index = index1 + 1;
        }
    }

    /**
     * Proper Identifier
     *
     * @param candidate
     * @return
     */
    public static boolean isIdentifier(@NotNull String candidate) {
        return StringUtil.isJavaIdentifier(candidate) && !JavaLexer.isKeyword(candidate, LanguageLevel.JDK_1_6);
    }

    @Override
    public void dispose() {

    }

    /**
     * Validates project configuration for module
     * @return
     * @throws ConfigurationException
     */
    @Override
    public boolean validate() throws ConfigurationException {
        if (StringUtil.isEmptyOrSpaces(packageField.getText()) ||
                !isValidFullyQualifiedJavaIdentifier(packageField.getText())) {
            throw new ConfigurationException("Base package is invalid");
        }

        return true;
    }

    /**
     * get component
     * @return
     */
    @Override
    public JComponent getComponent() {
        return main;
    }

    /**
     * Update data
     */
    @Override
    public void updateDataModel() {
        piJavaModuleBuilder.setPackageName(packageField.getText());
    }

    /**
     * set package field
     *
     * @return
     */
    public void setPackageName(String packageName) {
        if (packageField == null) {
            packageField = new JTextField();
        }
        packageField.setText(packageName);
    }

}
