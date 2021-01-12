package com.atsebak.embeddedlinuxjvm.runner.data;


import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class EmbeddedLinuxJVMRunConfigurationRunnerParameters implements Cloneable {
	public String moduleName;
    public String mainclass;
    private String hostname;
    private boolean runAsRoot;
    private boolean usingKey;
    @Nullable
    private String keyPath;
    @NotNull
    private String port = "1234";
    @NotNull
    private int sshPort = 22;
    private String username;
    @Nullable
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


