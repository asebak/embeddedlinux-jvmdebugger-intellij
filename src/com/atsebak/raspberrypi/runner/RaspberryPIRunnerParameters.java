package com.atsebak.raspberrypi.runner;


public class RaspberryPIRunnerParameters implements Cloneable {
    private String hostname;
    private boolean runAsRoot;
    private String display;
    private String port;
    private String username;
    private String password;

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

    @Override
    protected RaspberryPIRunnerParameters clone() {
        try {
            return (RaspberryPIRunnerParameters) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


