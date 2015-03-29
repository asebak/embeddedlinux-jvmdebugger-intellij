package com.atsebak.raspberrypi.ui;

import com.intellij.execution.*;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.remote.RemoteConfiguration;
import com.intellij.execution.remote.RemoteConfigurationType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class PIProcessChooserDialog extends DialogWrapper {
    @NonNls
    private static final String RUN_CONFIGURATION_NAME_PATTERN = "PI Debugger (%s)";

    private Project project;
    private JPanel contentPane = new JPanel();
    private JButton buttonOK = new JButton("Debug");
    private JButton buttonCancel = new JButton("Cancel");
    private JLabel hostnameText = new JLabel("Hostname");
    private JLabel porttext = new JLabel("Port");
    private JTextField hostname = new JFormattedTextField();
    private JTextField port = new JFormattedTextField();

    public PIProcessChooserDialog(@NotNull Project project) {
        super(project);
        this.project = project;
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });


// call onCancel() when cross is clicked
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                onCancel();
//            }
//        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        contentPane.add(hostnameText);
        contentPane.add(hostname);
        contentPane.add(porttext);
        contentPane.add(port);
        contentPane.add(buttonOK);
        contentPane.add(buttonCancel);
        onOK();
    }

    @NotNull
    private static String getRunConfigurationName(String debugPort) {
        return String.format(RUN_CONFIGURATION_NAME_PATTERN, debugPort);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    private void onOK() {
        final PropertiesComponent properties = PropertiesComponent.getInstance(project);

        hostname.setText("10.42.0.67");
        port.setText("765");
        final String debugPort = port.getText();

        closeOldSessionAndRun(debugPort);
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private RunnerAndConfigurationSettings createRunConfiguration(Project project, String debugPort) {
        final RemoteConfigurationType remoteConfigurationType = RemoteConfigurationType.getInstance();

        final ConfigurationFactory factory = remoteConfigurationType.getFactory();
        final RunnerAndConfigurationSettings runSettings =
                RunManager.getInstance(project).createRunConfiguration(getRunConfigurationName(debugPort), factory);
        final RemoteConfiguration configuration = (RemoteConfiguration) runSettings.getConfiguration();

        configuration.HOST = hostname.getText();
        configuration.PORT = debugPort;
        configuration.USE_SOCKET_TRANSPORT = true;
        configuration.SERVER_MODE = false;

        return runSettings;
    }

    private void closeOldSessionAndRun(final String debugPort) {
        final String configurationName = getRunConfigurationName(debugPort);
        final Collection<RunContentDescriptor> descriptors =
                ExecutionHelper.findRunningConsoleByTitle(project, new NotNullFunction<String, Boolean>() {
                    @NotNull
                    @Override
                    public Boolean fun(String title) {
                        return configurationName.equals(title);
                    }
                });

        if (descriptors.size() > 0) {
            final RunContentDescriptor descriptor = descriptors.iterator().next();
            final ProcessHandler processHandler = descriptor.getProcessHandler();
            final Content content = descriptor.getAttachedContent();

            if (processHandler != null && content != null) {
                final Executor executor = DefaultDebugExecutor.getDebugExecutorInstance();

                if (processHandler.isProcessTerminated()) {
                    ExecutionManager.getInstance(project).getContentManager()
                            .removeRunContent(executor, descriptor);
                } else {
                    content.getManager().setSelectedContent(content);
                    ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(executor.getToolWindowId());
                    window.activate(null, false, true);
                    return;
                }
            }
        }

        runSession(debugPort);
    }

    private void runSession(String debugPort) {
        final RunnerAndConfigurationSettings settings = createRunConfiguration(project, debugPort);
        ProgramRunnerUtil.executeConfiguration(project, settings, DefaultDebugExecutor.getDebugExecutorInstance());
    }
}
