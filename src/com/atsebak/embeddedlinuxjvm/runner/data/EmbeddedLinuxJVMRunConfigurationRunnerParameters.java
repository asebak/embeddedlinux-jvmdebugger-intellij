package com.atsebak.embeddedlinuxjvm.runner.data;


import lombok.Data;

@Data
public class EmbeddedLinuxJVMRunConfigurationRunnerParameters implements Cloneable {
    public String mainclass;
    private String hostname;
    private boolean runAsRoot;
    private String port;
    private String username;
    private String password;
    private String classesDirectory;
    private String vmParameters;
    private String programArguments;

    /**
     * Clones members
     *
     * @return
     */
    @Override
    public EmbeddedLinuxJVMRunConfigurationRunnerParameters clone() {
        try {
            return (EmbeddedLinuxJVMRunConfigurationRunnerParameters) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}


