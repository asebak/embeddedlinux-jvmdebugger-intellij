package com.atsebak.embeddedlinuxjvm.ui;

import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSH;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSHConnectionValidator;
import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.atsebak.embeddedlinuxjvm.runner.data.RaspberryPIRunnerParameters;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.execution.configurations.ConfigurationUtil;
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
import java.io.IOException;

public class RaspberryPIRunConfigurationEditor extends SettingsEditor<EmbeddedLinuxJVMRunConfiguration> implements PanelWithAnchor {
    private final ConfigurationModuleSelector myModuleSelector;
    private final Project myProject;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myMainClass;
    private LabeledComponent<JComboBox> myModule;
    private JPanel myGenericPanel;
    private JCheckBox runAsRootCheckBox;
    private JTextField debugPort;
    private JTextField hostName;
    private JPanel mainPanel;
    private JTextField username;
    private JPasswordField password;
    private JButton validateConnection;
    private JComponent myAnchor;

    /**
     * Constructor for run configuration form
     *
     * @param project
     */
    public RaspberryPIRunConfigurationEditor(final Project project) {
        myProject = project;
        validateConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    SSHConnectionValidator
                            .builder()
                            .ip(hostName.getText())
                            .password(password.getText())
                            .username(username.getText())
                            .build().checkSSHConnection(SSH.builder()
                            .connectionTimeout(1000)
                            .timeout(1000)
                            .build().toClient(), project);
                } catch (IOException e1) {

                }
            }
        });
        myModuleSelector = new ConfigurationModuleSelector(project, myModule.getComponent());
        myModule.getComponent().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        ClassBrowser.createApplicationClassBrowser(project, myModuleSelector).setField(getMainClassField());

        myAnchor = UIUtil.mergeComponentsWithAnchor(myMainClass, myModule);
    }

    /**
     * Called when application loaded/ when cancelling the settings
     * @param configuration
     */
    @Override
    protected void resetEditorFrom(EmbeddedLinuxJVMRunConfiguration configuration) {

        getMainClassField().setText(configuration.getRunnerParameters().getMainclass() != null ?
                configuration.getRunnerParameters().getMainclass().replaceAll("\\$", "\\.") : "");

        RaspberryPIRunnerParameters parameters = configuration.getRunnerParameters();
        hostName.setText(parameters.getHostname());
        runAsRootCheckBox.setSelected(parameters.isRunAsRoot());
        debugPort.setText(parameters.getPort());
        username.setText(parameters.getUsername());
        password.setText(parameters.getPassword());
    }

    /**
     * When you click the apply button
     * @param configuration
     * @throws ConfigurationException
     */
    @Override
    protected void applyEditorTo(EmbeddedLinuxJVMRunConfiguration configuration) throws ConfigurationException {
        final String className = getMainClassField().getText();
        final PsiClass aClass = myModuleSelector.findClass(className);


        configuration.getRunnerParameters().setMainclass(aClass != null ? JavaExecutionUtil.getRuntimeQualifiedName(aClass) : className);

        setPiSettings(configuration.getRunnerParameters());
    }

    /**
     * Specific settings for this runner
     * @param parameters
     */
    private void setPiSettings(RaspberryPIRunnerParameters parameters) {
        parameters.setHostname(hostName.getText());
        parameters.setPort(debugPort.getText());
        parameters.setRunAsRoot(runAsRootCheckBox.isSelected());
        parameters.setUsername(username.getText());
        parameters.setPassword(password.getText());
    }

    /**
     * Returns the layout
     * @return
     */
    @NotNull
    @Override
    protected JComponent createEditor() {
        return mainPanel;
    }

    /**
     * Creates UI Components
     */
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

    /**
     * Gets the main class to execute java app against
     * @return
     */
    public EditorTextFieldWithBrowseButton getMainClassField() {
        return myMainClass.getComponent();
    }

    /**
     * Anchor getter
     * @return
     */
    @Override
    public JComponent getAnchor() {
        return myAnchor;
    }

    /**
     * Anchor setter
     * @param anchor
     */
    @Override
    public void setAnchor(@Nullable JComponent anchor) {
        this.myAnchor = anchor;
        myMainClass.setAnchor(anchor);
        myModule.setAnchor(anchor);
    }
}

