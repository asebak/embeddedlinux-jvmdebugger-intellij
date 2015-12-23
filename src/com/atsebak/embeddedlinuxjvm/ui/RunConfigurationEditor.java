package com.atsebak.embeddedlinuxjvm.ui;

import com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.atsebak.embeddedlinuxjvm.protocol.ssh.SSHConnectionValidator;
import com.atsebak.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.atsebak.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.execution.configurations.ConfigurationUtil;
import com.intellij.execution.ui.ClassBrowser;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiMethodUtil;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.util.ui.UIUtil;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

public class RunConfigurationEditor extends SettingsEditor<EmbeddedLinuxJVMRunConfiguration> implements PanelWithAnchor {
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
    private RawCommandLineEditor vmParameters;
    private RawCommandLineEditor programArguments;
    private JLabel sshStatus;
    private JCheckBox usingKey;
    private JTextField keyfile;
    private JButton selectPrivateKeyButton;
    private JComponent myAnchor;

    /**
     * Constructor for run configuration form
     *
     * @param project
     */
    public RunConfigurationEditor(final Project project) {
        myProject = project;
        validateConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                    public void run() {
                        final ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                        if (progressIndicator != null) {
                            progressIndicator.setText(EmbeddedLinuxJVMBundle.getString("ssh.tryingtoconnect"));
                            progressIndicator.setIndeterminate(true);
                        }

                        SSHConnectionValidator.SSHConnectionState validator = SSHConnectionValidator
                                .builder()
                                .ip(hostName.getText())
                                .password(new String(password.getPassword()))
                                .username(username.getText())
                                .useKey(usingKey.isSelected())
                                .key(keyfile.getText())
                                .build().checkSSHConnection();

                        sshStatus.setVisible(true);
                        if (validator.isConnected()) {
                            sshStatus.setText(EmbeddedLinuxJVMBundle.getString("ssh.connection.success"));
                            sshStatus.setForeground(Color.GREEN);
                        } else {
                            sshStatus.setText(EmbeddedLinuxJVMBundle.getString("ssh.remote.error") + ": " + validator.getMessage());
                            sshStatus.setForeground(Color.RED);
                        }
                    }
                }, EmbeddedLinuxJVMBundle.getString("pi.validatingconnection"), true, myProject);

            }
        });

        keyFileStateChange();

        myModuleSelector = new ConfigurationModuleSelector(project, myModule.getComponent());

        myModule.getComponent().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        final JFileChooser keyChooser = new JFileChooser();
        keyChooser.setDialogTitle(EmbeddedLinuxJVMBundle.getString("ssh.dialog.title"));
        keyChooser.setMultiSelectionEnabled(false);
        keyChooser.setFileHidingEnabled(false);
        selectPrivateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton) e.getSource();
                if (selectPrivateKeyButton == button) {
                    int returnVal = keyChooser.showOpenDialog(myGenericPanel);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = keyChooser.getSelectedFile();
                        keyfile.setText(file.getAbsolutePath());
                    }
                }
            }
        });
        usingKey.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if (usingKey == source) {
                    keyFileStateChange();
                }
            }
        });
        PromptSupport.setPrompt(EmbeddedLinuxJVMBundle.getString("debugport.placeholder"), debugPort);
        PromptSupport.setPrompt(EmbeddedLinuxJVMBundle.getString("ssh.privatekey.ph"), keyfile);
        ClassBrowser.createApplicationClassBrowser(project, myModuleSelector).setField(getMainClassField());

        myAnchor = UIUtil.mergeComponentsWithAnchor(myMainClass, myModule);
    }

    @Override
    protected void disposeEditor() {
        super.disposeEditor();
    }

    private void keyFileStateChange() {
        if (!usingKey.isSelected()) {
            keyfile.setEnabled(false);
            password.setEnabled(true);
            selectPrivateKeyButton.setEnabled(false);
        } else {
            keyfile.setEnabled(false);
            password.setEnabled(false);
            selectPrivateKeyButton.setEnabled(true);
        }
    }

    /**
     * Called when application loaded/ when cancelling the settings
     * @param configuration
     */
    @Override
    protected void resetEditorFrom(EmbeddedLinuxJVMRunConfiguration configuration) {

        getMainClassField().setText(configuration.getRunnerParameters().getMainclass() != null ?
                configuration.getRunnerParameters().getMainclass().replaceAll("\\$", "\\.") : "");

        EmbeddedLinuxJVMRunConfigurationRunnerParameters parameters = configuration.getRunnerParameters();
        vmParameters.setDialogCaption(EmbeddedLinuxJVMBundle.getString("app.vmoptions"));
        vmParameters.setText(parameters.getVmParameters());
        programArguments.setDialogCaption(EmbeddedLinuxJVMBundle.getString("app.programargs"));
        programArguments.setText(parameters.getProgramArguments());
        hostName.setText(parameters.getHostname());
        runAsRootCheckBox.setSelected(parameters.isRunAsRoot());
        usingKey.setSelected(parameters.isUsingKey());
        keyfile.setText(parameters.getKeyPath());
        debugPort.setText(parameters.getPort());
        username.setText(parameters.getUsername());
        password.setText(parameters.getPassword());
        sshStatus.setVisible(false);
        keyFileStateChange();
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

        setSettings(configuration.getRunnerParameters());
    }

    /**
     * Specific settings for this runner
     * @param parameters
     */
    private void setSettings(EmbeddedLinuxJVMRunConfigurationRunnerParameters parameters) {
        parameters.setHostname(hostName.getText());
        parameters.setPort(debugPort.getText());
        parameters.setRunAsRoot(runAsRootCheckBox.isSelected());
        parameters.setUsername(username.getText());
        parameters.setPassword(new String(password.getPassword()));
        parameters.setVmParameters(vmParameters.getText());
        parameters.setProgramArguments(programArguments.getText());
        parameters.setUsingKey(usingKey.isSelected());
        parameters.setKeyPath(keyfile.getText());
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

