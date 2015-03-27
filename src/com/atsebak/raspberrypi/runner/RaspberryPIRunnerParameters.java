package com.atsebak.raspberrypi.runner;

import org.jetbrains.annotations.NotNull;

public class RaspberryPIRunnerParameters implements Cloneable {
    @NotNull
    private String hostname;
    private boolean runAsRoot;
    @NotNull
    private String display;
    @NotNull
    private String port;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public boolean isRunAsRoot() {
        return runAsRoot;
    }

    public void setRunAsRoot(boolean runAsRoot) {
        this.runAsRoot = runAsRoot;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    //    private String piUsername;
//    private String piPassword;
    @Override
    protected RaspberryPIRunnerParameters clone() {
        try {
            return (RaspberryPIRunnerParameters) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}


