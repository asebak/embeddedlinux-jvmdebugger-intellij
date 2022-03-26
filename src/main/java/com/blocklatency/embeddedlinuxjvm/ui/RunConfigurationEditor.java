package com.blocklatency.embeddedlinuxjvm.ui;

import com.blocklatency.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle;
import com.blocklatency.embeddedlinuxjvm.protocol.ssh.SSHConnectionValidator;
import com.blocklatency.embeddedlinuxjvm.runner.conf.EmbeddedLinuxJVMRunConfiguration;
import com.blocklatency.embeddedlinuxjvm.runner.data.EmbeddedLinuxJVMRunConfigurationRunnerParameters;
import com.intellij.application.options.ModulesComboBox;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.execution.configurations.ConfigurationUtil;
import com.intellij.execution.ui.ClassBrowser;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiMethodUtil;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.util.ui.UIUtil;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunConfigurationEditor extends SettingsEditor<EmbeddedLinuxJVMRunConfiguration> implements PanelWithAnchor {
    private final ConfigurationModuleSelector myModuleSelector;
    private final Project myProject;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myMainClass;
    private LabeledComponent<ModulesComboBox> myModule;
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
    private JTextField sshPort;
    private JComponent myAnchor;

    /**
     * Constructor for run configuration form
     *
     * @param project
     */
    public RunConfigurationEditor(final Project project) {
        myProject = project;
        validateConnection.addActionListener(e -> ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {
                final ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                if (progressIndicator != null) {
                    progressIndicator.setText(EmbeddedLinuxJVMBundle.getString("ssh.tryingtoconnect"));
                    progressIndicator.setIndeterminate(true);
                }

                SSHConnectionValidator.SSHConnectionState validator = SSHConnectionValidator
                        .builder()
                        .port(Integer.valueOf(sshPort.getText()))
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
        }, EmbeddedLinuxJVMBundle.getString("pi.validatingconnection"), true, myProject));

        keyFileStateChange();

        myModule.getComponent().fillModules(project);
        myModuleSelector = new ConfigurationModuleSelector(project, myModule.getComponent());

        myModule.getComponent().addActionListener(e -> {
        });
        final JFileChooser keyChooser = new JFileChooser();
        keyChooser.setDialogTitle(EmbeddedLinuxJVMBundle.getString("ssh.dialog.title"));
        keyChooser.setMultiSelectionEnabled(false);
        keyChooser.setFileHidingEnabled(false);
        selectPrivateKeyButton.addActionListener(e -> {
            JButton button = (JButton) e.getSource();
            if (selectPrivateKeyButton == button) {
                int returnVal = keyChooser.showOpenDialog(mainPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = keyChooser.getSelectedFile();
                    keyfile.setText(file.getAbsolutePath());
                }
            }
        });
        usingKey.addItemListener(e -> {
            JCheckBox source = (JCheckBox) e.getSource();
            if (usingKey == source) {
                keyFileStateChange();
            }
        });
        PromptSupport.setPrompt(EmbeddedLinuxJVMBundle.getString("debugport.placeholder"), debugPort);
        PromptSupport.setPrompt(EmbeddedLinuxJVMBundle.getString("ssh.privatekey.ph"), keyfile);
        ClassBrowser.createApplicationClassBrowser(project, myModuleSelector).setField(getMainClassField());
       // new ClassBrowser.AppClassBrowser(project, myModuleSelector).setField(getMainClassField());

        myAnchor = UIUtil.mergeComponentsWithAnchor(myMainClass, myModule);

        //restrict only numbers
        ((AbstractDocument) sshPort.getDocument()).setDocumentFilter(new DocumentFilter() {
            Pattern regEx = Pattern.compile("\\d+");
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if (!matcher.matches()) {
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });

        ((AbstractDocument) debugPort.getDocument()).setDocumentFilter(new DocumentFilter() {
            Pattern regEx = Pattern.compile("\\d+");
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if (!matcher.matches()) {
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });
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
        Module moduleParam = null;
        if (parameters.getModuleName() != null && parameters.getModuleName().length() > 0) {
            moduleParam = ModuleManager.getInstance(myProject).findModuleByName(parameters.getModuleName());
        }
        vmParameters.setText(parameters.getVmParameters());
        programArguments.setText(parameters.getProgramArguments());
        hostName.setText(parameters.getHostname());
        runAsRootCheckBox.setSelected(parameters.isRunAsRoot());
        usingKey.setSelected(parameters.isUsingKey());
        keyfile.setText(parameters.getKeyPath());
        debugPort.setText(parameters.getPort());
        username.setText(parameters.getUsername());
        password.setText(parameters.getPassword());
        sshPort.setText(Integer.toString(parameters.getSshPort()));
        sshStatus.setVisible(false);
        myModule.getComponent().setSelectedModule(moduleParam);
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
        String moduleName = "";
        if (myModule.getComponent().getSelectedModule() != null) {
            moduleName = myModule.getComponent().getSelectedModule().getName();
        }
        parameters.setHostname(hostName.getText());
        parameters.setPort(debugPort.getText());
        parameters.setRunAsRoot(runAsRootCheckBox.isSelected());
        parameters.setUsername(username.getText());
        parameters.setPassword(new String(password.getPassword()));
        parameters.setVmParameters(vmParameters.getText());
        parameters.setProgramArguments(programArguments.getText());
        parameters.setUsingKey(usingKey.isSelected());
        parameters.setKeyPath(keyfile.getText());
        parameters.setModuleName(moduleName);
        if(org.apache.commons.lang.StringUtils.isEmpty(sshPort.getText())) {
            sshPort.setText("22");
        }
        parameters.setSshPort(Integer.parseInt(sshPort.getText()));
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
        myMainClass = new LabeledComponent<>();
        myMainClass.setComponent(new EditorTextFieldWithBrowseButton(myProject, true, (declaration, place) -> {
            if (declaration instanceof PsiClass) {
                final PsiClass aClass = (PsiClass)declaration;
                if (ConfigurationUtil.MAIN_CLASS.value(aClass) && PsiMethodUtil.findMainMethod(aClass) != null || place.getParent() != null && myModuleSelector.findClass(((PsiClass)declaration).getQualifiedName()) != null) {
                    return JavaCodeFragment.VisibilityChecker.Visibility.VISIBLE;
                }
            }
            return JavaCodeFragment.VisibilityChecker.Visibility.NOT_VISIBLE;
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

