package com.atsebak.raspberrypi.runner;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("unused")
public class RaspberryPIRunConfigurationEditor extends SettingsEditor<RaspberryPIRunConfiguration> {
    private JPanel main;
    private JTabbedPane tabbedPane;

    public RaspberryPIRunConfigurationEditor() {
       // myBrowserSelector = new BrowserSelector();
       // main.add(BorderLayout.CENTER, myBrowserSelector.getMainComponent());
    }
    @Override
    protected void resetEditorFrom(RaspberryPIRunConfiguration s) {
//        RaspberryPIRunnerParameters params = s.getRunnerParameters();
//        myWebPathField.setText(params.getUrl());
        //myBrowserSelector.setSelected(params.getNonDefaultBrowser() != null ? params.getNonDefaultBrowser() : null);
    }
    @Override
    protected void applyEditorTo(RaspberryPIRunConfiguration configuration) throws ConfigurationException {
        RaspberryPIRunnerParameters runnerParameters = configuration.getRunnerParameters();
//        RaspberryPIRunnerParameters params = s.getRunnerParameters();
//        params.setUrl(myWebPathField.getText());
//        params.setNonDefaultBrowser(myBrowserSelector.getSelected());
    }
    @NotNull
    @Override
    protected JComponent createEditor() {
        return main;
    }
}

