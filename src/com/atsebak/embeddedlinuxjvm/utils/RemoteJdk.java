package com.atsebak.embeddedlinuxjvm.utils;



public class RemoteJdk {

    /**
     * Sets up a remote command line
     * @param exePath
     * @return
     */
    public static RemoteCommandLine setupJVMCommandLine(final String exePath) {
        final RemoteCommandLine commandLine = new RemoteCommandLine();
        commandLine.setExePath(exePath);
        return commandLine;
    }

}
