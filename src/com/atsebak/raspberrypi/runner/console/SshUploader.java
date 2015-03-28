package com.atsebak.raspberrypi.runner.console;

import com.atsebak.raspberrypi.protocol.ssh.SSHHandler;
import com.atsebak.raspberrypi.runner.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.application.ApplicationManager;

import java.io.File;
import java.io.IOException;

public class SshUploader {
    public void uploadToTarget(final RaspberryPIRunnerParameters rp, final File outputDirectory) throws RuntimeConfigurationException, IOException, ClassNotFoundException {
        ApplicationManager.getApplication()
                .executeOnPooledThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SSHHandler sshHandler = new SSHHandler(rp.getHostname(), rp.getUsername(), rp.getPassword());
                            sshHandler.upload(outputDirectory);
                        } catch (Exception e) {

                        }
                    }
                });
    }
}


