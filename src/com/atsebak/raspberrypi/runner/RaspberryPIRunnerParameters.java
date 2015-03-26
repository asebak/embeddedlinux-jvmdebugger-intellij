package com.atsebak.raspberrypi.runner;

import lombok.Getter;
import lombok.Setter;

public class RaspberryPIRunnerParameters implements Cloneable {
    private String hostname;
    private boolean runAsRoot;
    private String piUsername;
    private String piPassword;
    @Override
    protected RaspberryPIRunnerParameters clone() {
        try {
            return (RaspberryPIRunnerParameters) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}


