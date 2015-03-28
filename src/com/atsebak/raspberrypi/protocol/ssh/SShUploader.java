package com.atsebak.raspberrypi.protocol.ssh;

import com.atsebak.raspberrypi.runner.RaspberryPIRunnerParameters;
import com.intellij.execution.configurations.RuntimeConfigurationException;

import java.io.File;
import java.io.IOException;

public class SShUploader {
    public void uploadToTarget(final RaspberryPIRunnerParameters rp, final File outputDirectory, final String cmd) throws RuntimeConfigurationException, IOException, ClassNotFoundException {
//        ApplicationManager.getApplication()
//                .executeOnPooledThread(new Runnable() {
//                    @Override
//                    public void run() {
                        try {
                            SSHHandler sshHandler = new SSHHandler(rp.getHostname(), rp.getUsername(), rp.getPassword());
                            sshHandler.upload(outputDirectory, cmd);
//                            sshHandler.executeCommand();
                        } catch (Exception e) {

                        }
//                    }
//                });
    }
}


