package com.atsebak.raspberrypi.ui;

import com.atsebak.raspberrypi.runner.RaspberryPIRunConfiguration;
import com.atsebak.raspberrypi.runner.RaspberryPIRunConfigurationModule;
import com.atsebak.raspberrypi.runner.RaspberryPIRunnerParameters;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.execution.configurations.ConfigurationUtil;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.ui.ClassBrowser;
import com.intellij.execution.ui.ConfigurationModuleSelector;
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
    private final Project myProject;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myMainClass;
    private LabeledComponent<JComboBox> myModule;
    private JPanel myGenericPanel;
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
//        myCommonProgramParameters.setModuleContext(myModuleSelector.getModule());
        myModule.getComponent().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String x = "";
//                myCommonProgramParameters.setModuleContext(myModuleSelector.getModule());
            }
        });
        ClassBrowser.createApplicationClassBrowser(project, myModuleSelector).setField(getMainClassField());
//        myVersionDetector = new JreVersionDetector();

        myAnchor = UIUtil.mergeComponentsWithAnchor(myMainClass, myModule);

//        myAnchor = UIUtil.mergeComponentsWithAnchor(myMainClass, myCommonProgramParameters, myAlternativeJREPanel, myModule);
    }
    @Override
    protected void resetEditorFrom(RaspberryPIRunConfiguration configuration) {
//        myCommonProgramParameters.reset(configuration);
        RaspberryPIRunConfigurationModule module = new RaspberryPIRunConfigurationModule(configuration.getName(),
                new JavaRunConfigurationModule(configuration.getProject(), false), configuration.getFactory());
        myModuleSelector.reset(module);

        getMainClassField().setText(configuration.getRunnerParameters().getMainclass() != null ? configuration.getRunnerParameters().getMainclass().replaceAll("\\$", "\\.") : "");

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
//        myCommonProgramParameters.applyTo(configuration);
        RaspberryPIRunConfigurationModule module = new RaspberryPIRunConfigurationModule(configuration.getName(),
                new JavaRunConfigurationModule(configuration.getProject(), false), configuration.getFactory());

        myModuleSelector.applyTo(module);
        final String className = getMainClassField().getText();
        final PsiClass aClass = myModuleSelector.findClass(className);


        configuration.getRunnerParameters().setMainclass(aClass != null ? JavaExecutionUtil.getRuntimeQualifiedName(aClass) : className);

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

    @Override
    public JComponent getAnchor() {
        return myAnchor;
    }

    @Override
    public void setAnchor(@Nullable JComponent anchor) {
        this.myAnchor = anchor;
        myMainClass.setAnchor(anchor);
        myModule.setAnchor(anchor);
    }
}

