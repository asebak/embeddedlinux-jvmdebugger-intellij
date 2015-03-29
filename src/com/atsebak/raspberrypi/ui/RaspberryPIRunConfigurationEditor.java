package com.atsebak.raspberrypi.ui;

import com.atsebak.raspberrypi.runner.RaspberryPIRunConfiguration;
import com.atsebak.raspberrypi.runner.RaspberryPIRunnerParameters;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.execution.configurations.ConfigurationUtil;
import com.intellij.execution.ui.AlternativeJREPanel;
import com.intellij.execution.ui.ClassBrowser;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.execution.util.JreVersionDetector;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiMethodUtil;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RaspberryPIRunConfigurationEditor extends SettingsEditor<RaspberryPIRunConfiguration> implements PanelWithAnchor {
    private final ConfigurationModuleSelector myModuleSelector;
    private final JreVersionDetector myVersionDetector;
    private final Project myProject;
    private CommonJavaParametersPanel myCommonProgramParameters;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myMainClass;
    private LabeledComponent<JComboBox> myModule;
    private JPanel myGenericPanel;
    private AlternativeJREPanel myAlternativeJREPanel;
    private JCheckBox myShowSwingInspectorCheckbox;
    private JCheckBox runAsRootCheckBox;
    private JTextField xDisplay;
    private JTextField debugPort;
    private JTextField hostName;
    private JPanel mainPanel;
    private JTextField username;
    private JPasswordField password;
    private JComponent myAnchor;

    public RaspberryPIRunConfigurationEditor(final Project project) {
        myProject = project;
        myModuleSelector = new ConfigurationModuleSelector(project, myModule.getComponent());
        myCommonProgramParameters.setModuleContext(myModuleSelector.getModule());
        myModule.getComponent().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myCommonProgramParameters.setModuleContext(myModuleSelector.getModule());
            }
        });
        ClassBrowser.createApplicationClassBrowser(project, myModuleSelector).setField(getMainClassField());
        myVersionDetector = new JreVersionDetector();

        myAnchor = UIUtil.mergeComponentsWithAnchor(myMainClass, myCommonProgramParameters, myAlternativeJREPanel, myModule);
    }
    @Override
    protected void resetEditorFrom(RaspberryPIRunConfiguration configuration) {
        myCommonProgramParameters.reset(configuration);
        myModuleSelector.reset(configuration);
        getMainClassField().setText(configuration.MAIN_CLASS_NAME != null ? configuration.MAIN_CLASS_NAME.replaceAll("\\$", "\\.") : "");
        myAlternativeJREPanel.init(configuration.ALTERNATIVE_JRE_PATH, configuration.ALTERNATIVE_JRE_PATH_ENABLED);

        updateShowSwingInspector(configuration);

        RaspberryPIRunnerParameters parameters = configuration.getRunnerParameters();
        hostName.setText(parameters.getHostname());
        runAsRootCheckBox.setSelected(parameters.isRunAsRoot());
        xDisplay.setText(parameters.getDisplay());
        debugPort.setText(parameters.getPort());
        username.setText(parameters.getUsername());
        password.setText(parameters.getPassword());
    }
    @Override
    protected void applyEditorTo(RaspberryPIRunConfiguration configuration) throws ConfigurationException {
        myCommonProgramParameters.applyTo(configuration);
        myModuleSelector.applyTo(configuration);
        final String className = getMainClassField().getText();
        final PsiClass aClass = myModuleSelector.findClass(className);
        configuration.MAIN_CLASS_NAME = aClass != null ? JavaExecutionUtil.getRuntimeQualifiedName(aClass) : className;
        configuration.ALTERNATIVE_JRE_PATH = myAlternativeJREPanel.getPath();
        configuration.ALTERNATIVE_JRE_PATH_ENABLED = myAlternativeJREPanel.isPathEnabled();
        configuration.ENABLE_SWING_INSPECTOR = (myVersionDetector.isJre50Configured(configuration)
                || myVersionDetector.isModuleJre50Configured(configuration)) && myShowSwingInspectorCheckbox.isSelected();
        updateShowSwingInspector(configuration);

        setPiSettings(configuration.getRunnerParameters());


    }

    private void setPiSettings(RaspberryPIRunnerParameters parameters) {
        parameters.setHostname(hostName.getText());
        parameters.setDisplay(xDisplay.getText());
        parameters.setPort(debugPort.getText());
        parameters.setRunAsRoot(runAsRootCheckBox.isSelected());
        parameters.setUsername(username.getText());
        parameters.setPassword(password.getText());
    }

    private void updateShowSwingInspector(final RaspberryPIRunConfiguration configuration) {
        if (myVersionDetector.isJre50Configured(configuration) || myVersionDetector.isModuleJre50Configured(configuration)) {
            myShowSwingInspectorCheckbox.setEnabled(true);
            myShowSwingInspectorCheckbox.setSelected(configuration.ENABLE_SWING_INSPECTOR);
            myShowSwingInspectorCheckbox.setText(ExecutionBundle.message("show.swing.inspector"));
        }
        else {
            myShowSwingInspectorCheckbox.setEnabled(false);
            myShowSwingInspectorCheckbox.setSelected(false);
            myShowSwingInspectorCheckbox.setText(ExecutionBundle.message("show.swing.inspector.disabled"));
        }
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return mainPanel;
    }

    private void createUIComponents() {
        myMainClass = new LabeledComponent<EditorTextFieldWithBrowseButton>();
        myMainClass.setComponent(new EditorTextFieldWithBrowseButton(myProject, true, new JavaCodeFragment.VisibilityChecker() {
            @Override
            public Visibility isDeclarationVisible(PsiElement declaration, PsiElement place) {
                if (declaration instanceof PsiClass) {
                    final PsiClass aClass = (PsiClass)declaration;
                    if (ConfigurationUtil.MAIN_CLASS.value(aClass) && PsiMethodUtil.findMainMethod(aClass) != null || place.getParent() != null && myModuleSelector.findClass(((PsiClass)declaration).getQualifiedName()) != null) {
                        return Visibility.VISIBLE;
                    }
                }
                return Visibility.NOT_VISIBLE;
            }
        }));
    }
    public EditorTextFieldWithBrowseButton getMainClassField() {
        return myMainClass.getComponent();
    }

    public CommonJavaParametersPanel getCommonProgramParameters() {
        return myCommonProgramParameters;
    }

    @Override
    public JComponent getAnchor() {
        return myAnchor;
    }

    @Override
    public void setAnchor(@Nullable JComponent anchor) {
        this.myAnchor = anchor;
        myMainClass.setAnchor(anchor);
        myCommonProgramParameters.setAnchor(anchor);
        myAlternativeJREPanel.setAnchor(anchor);
        myModule.setAnchor(anchor);
    }
}

