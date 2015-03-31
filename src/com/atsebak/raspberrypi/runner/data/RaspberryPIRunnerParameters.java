package com.atsebak.raspberrypi.runner.data;


import lombok.Data;

@Data
public class RaspberryPIRunnerParameters implements Cloneable {
    public String mainclass;
    private String hostname;
    private boolean runAsRoot;
    private String display;
    private String port;
    private String username;
    private String password;
    private String classesDirectory;

    /**
     * Clones members
     *
     * @return
     */
    @Override
    protected RaspberryPIRunnerParameters clone() {
        try {
            return (RaspberryPIRunnerParameters) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}


