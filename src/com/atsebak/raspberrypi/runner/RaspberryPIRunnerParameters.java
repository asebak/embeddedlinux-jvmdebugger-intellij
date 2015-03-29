package com.atsebak.raspberrypi.runner;


import java.util.LinkedHashMap;
import java.util.Map;

public class RaspberryPIRunnerParameters implements Cloneable {
    /*IDEA Standard Configuration*/
    private final Map<String, String> envs = new LinkedHashMap<String, String>();
    public String mainclass;
    public String vmParameters;
    public String programParameters;
    public String workingDirectory;
    public boolean alternateJrePathEnabled;
    public String alternateJrePath;
    public boolean enableSwingInspector;
    public boolean passParentEnv;
    private String hostname;
    private boolean runAsRoot;
    private String display;
    private String port;
    private String username;
    private String password;

    public Map<String, String> getEnvs() {
        return envs;
    }

    public String getMainclass() {
        return mainclass;
    }

    public void setMainclass(String mainclass) {
        this.mainclass = mainclass;
    }

    public String getVmParameters() {
        return vmParameters;
    }

    public void setVmParameters(String vmParameters) {
        this.vmParameters = vmParameters;
    }

    public String getProgramParameters() {
        return programParameters;
    }

    public void setProgramParameters(String programParameters) {
        this.programParameters = programParameters;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public boolean isAlternateJrePathEnabled() {
        return alternateJrePathEnabled;
    }

    public void setAlternateJrePathEnabled(boolean alternateJrePathEnabled) {
        this.alternateJrePathEnabled = alternateJrePathEnabled;
    }

    public String getAlternateJrePath() {
        return alternateJrePath;
    }

    public void setAlternateJrePath(String alternateJrePath) {
        this.alternateJrePath = alternateJrePath;
    }

    public boolean isEnableSwingInspector() {
        return enableSwingInspector;
    }

    public void setEnableSwingInspector(boolean enableSwingInspector) {
        this.enableSwingInspector = enableSwingInspector;
    }

    public boolean isPassParentEnv() {
        return passParentEnv;
    }

    public void setPassParentEnv(boolean passParentEnv) {
        this.passParentEnv = passParentEnv;
    }

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


