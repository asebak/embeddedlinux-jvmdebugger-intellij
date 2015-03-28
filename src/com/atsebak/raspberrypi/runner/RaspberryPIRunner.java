package com.atsebak.raspberrypi.runner;

import com.atsebak.raspberrypi.protocol.ssh.SSHHandler;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.util.io.URLUtil;
import com.intellij.util.net.HttpConfigurable;
import com.intellij.util.net.IdeaWideProxySelector;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class RaspberryPIRunner extends DefaultProgramRunner {
    private static final String RUNNER_ID = "RaspberryPIRunner";
    private static final String[] OUTPUT_DIRECTORIES = {"out", "target"};
    private static boolean isSshUrlExcluded(@NotNull HttpConfigurable httpConfigurable, @NotNull String url) {
        String host = URLUtil.parseHostFromSshUrl(url);
        return ((IdeaWideProxySelector) httpConfigurable.getOnlyBySettingsSelector()).isProxyException(host);
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState profileState, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        final RunProfile runProfileRaw = environment.getRunProfile();
        if (runProfileRaw instanceof RaspberryPIRunConfiguration) {
            RaspberryPIRunnerParameters runnerParameters = ((RaspberryPIRunConfiguration) runProfileRaw).getRunnerParameters();


            Module module = ((RaspberryPIRunConfiguration) runProfileRaw).getConfigurationModule().getModule();
            File outputDirectory = getOutputDirectory(module.getModuleFile().getParent().getPath());
            buildSshClient(runnerParameters, outputDirectory);
//            environment.getProject().getBaseDir()
        }
        else {
            return super.doExecute(profileState, environment);
        }
        throw new NotImplementedException();
    }

    private File getOutputDirectory(String relPath) {
        //todo fix to come up with smart way to get output files
        for (String s : OUTPUT_DIRECTORIES) {
            File output = new File(relPath + File.separator + s);
            if (output.exists()) {
                return output;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return (DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) || DefaultRunExecutor.EXECUTOR_ID.equals(executorId)) &&
                profile instanceof RaspberryPIRunConfiguration;
    }

    private void buildSshClient(RaspberryPIRunnerParameters rp, File outputDirectory) {
        try {

            SSHHandler sshHandler = new SSHHandler(rp.getHostname(), rp.getUsername(), rp.getPassword());
            sshHandler.upload();
//            GitXmlRpcSshService ssh = ServiceManager.getService(GitXmlRpcSshService.class);
//            final GeneralCommandLine myCommandLine = new GeneralCommandLine();
//            Map<String, String> myEnv = new HashMap<String, String>(EnvironmentUtil.getEnvironmentMap());
//            myEnv.put(GitSSHHandler.GIT_SSH_ENV, ssh.getScriptPath().getPath());
//            int myHandlerNo = ssh.registerHandler(new GitSSHGUIHandler(p));
//            myEnv.put(GitSSHHandler.SSH_HANDLER_ENV, Integer.toString(myHandlerNo));
//            int port = ssh.getXmlRcpPort();
//            myEnv.put(GitSSHHandler.SSH_PORT_ENV, Integer.toString(port));
//
//            final HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
//            boolean useHttpProxy = httpConfigurable.USE_HTTP_PROXY && !isSshUrlExcluded(httpConfigurable, null);
//            myEnv.put(GitSSHHandler.SSH_USE_PROXY_ENV, String.valueOf(useHttpProxy));
//
//            if (useHttpProxy) {
//                myEnv.put(GitSSHHandler.SSH_PROXY_HOST_ENV, StringUtil.notNullize(httpConfigurable.PROXY_HOST));
//                myEnv.put(GitSSHHandler.SSH_PROXY_PORT_ENV, String.valueOf(httpConfigurable.PROXY_PORT));
//                boolean proxyAuthentication = httpConfigurable.PROXY_AUTHENTICATION;
//                myEnv.put(GitSSHHandler.SSH_PROXY_AUTHENTICATION_ENV, String.valueOf(proxyAuthentication));
//
//                if (proxyAuthentication) {
//                    myEnv.put(GitSSHHandler.SSH_PROXY_USER_ENV, StringUtil.notNullize(httpConfigurable.PROXY_LOGIN));
//                    myEnv.put(GitSSHHandler.SSH_PROXY_PASSWORD_ENV, StringUtil.notNullize(httpConfigurable.getPlainProxyPassword()));
//                }
//            }
//            SSHMain main = new SSHMain(myEnv, rp.getHostname(), rp.getUsername(), Integer.parseInt(rp.getPort()), "");
//            main.start();
        } catch (Exception e) {

        }
    }
}
