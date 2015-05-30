package com.atsebak.embeddedlinuxjvm.utils;


import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.SimpleJavaParameters;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.util.PathUtil;
import com.intellij.util.lang.UrlClassLoader;
import gnu.trove.THashMap;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

public class RemoteJdk {
    private static final Logger LOG = Logger.getInstance("#com.atsebak.embeddedlinuxjvm.utils.JdkUtil");
    private static final String WRAPPER_CLASS = "com.intellij.rt.execution.CommandLineWrapper";

    public static RemoteCommandLine setupJVMCommandLine(final String exePath,
                                                         final SimpleJavaParameters javaParameters,
                                                         final boolean forceDynamicClasspath) {
        final RemoteCommandLine commandLine = new RemoteCommandLine();
        commandLine.setExePath(exePath);

//        final ParametersList vmParametersList = javaParameters.getVMParametersList();
//        commandLine.getEnvironment().putAll(javaParameters.getEnv());
//        commandLine.setPassParentEnvironment(javaParameters.isPassParentEnvs());
//
//        final Class commandLineWrapper;
//        if ((commandLineWrapper = getCommandLineWrapperClass()) != null) {
//            if (forceDynamicClasspath) {
//                File classpathFile = null;
//                File vmParamsFile = null;
//                if (!vmParametersList.hasParameter("-classpath") && !vmParametersList.hasParameter("-cp")) {
//                    if (javaParameters.isDynamicVMOptions() && useDynamicVMOptions()) {
//                        try {
//                            vmParamsFile = FileUtil.createTempFile("vm_params", null);
//                            final PrintWriter writer = new PrintWriter(vmParamsFile);
//                            try {
//                                for (String param : vmParametersList.getList()) {
//                                    if (param.startsWith("-D")) {
//                                        writer.println(param);
//                                    }
//                                }
//                            }
//                            finally {
//                                writer.close();
//                            }
//                        }
//                        catch (IOException e) {
//                            LOG.error(e);
//                        }
//                        final List<String> list = vmParametersList.getList();
//                        for (String param : list) {
//                            if (!param.trim().startsWith("-D")) {
//                                commandLine.addParameter(param);
//                            }
//                        }
//                    }
//                    else {
//                        commandLine.addParameters(vmParametersList.getList());
//                    }
//                    try {
//                        classpathFile = FileUtil.createTempFile("classpath", null);
//                        final PrintWriter writer = new PrintWriter(classpathFile);
//                        try {
//                            for (String path : javaParameters.getClassPath().getPathList()) {
//                                writer.println(path);
//                            }
//                        }
//                        finally {
//                            writer.close();
//                        }
//
//                        String classpath = PathUtil.getJarPathForClass(commandLineWrapper);
//                        final String utilRtPath = PathUtil.getJarPathForClass(StringUtilRt.class);
//                        if (!classpath.equals(utilRtPath)) {
//                            classpath += File.pathSeparator + utilRtPath;
//                        }
//                        final Class<UrlClassLoader> ourUrlClassLoader = UrlClassLoader.class;
//                        if (ourUrlClassLoader.getName().equals(vmParametersList.getPropertyValue("java.system.class.loader"))) {
//                            classpath += File.pathSeparator + PathUtil.getJarPathForClass(ourUrlClassLoader);
//                            classpath += File.pathSeparator + PathUtil.getJarPathForClass(THashMap.class);
//                        }
//
//                        commandLine.addParameter("-classpath");
//                        commandLine.addParameter(classpath);
//                    }
//                    catch (IOException e) {
//                        LOG.error(e);
//                    }
//                }
//
//                appendEncoding(javaParameters, commandLine, vmParametersList);
//                if (classpathFile != null) {
//                    commandLine.addParameter(commandLineWrapper.getName());
//                    commandLine.addParameter(classpathFile.getAbsolutePath());
//                }
//
//                if (vmParamsFile != null) {
//                    commandLine.addParameter("@vm_params");
//                    commandLine.addParameter(vmParamsFile.getAbsolutePath());
//                }
//            }
//            else {
//                appendParamsEncodingClasspath(javaParameters, commandLine, vmParametersList);
//            }
//        }
//        else {
//            appendParamsEncodingClasspath(javaParameters, commandLine, vmParametersList);
//        }
//
//        final String mainClass = javaParameters.getMainClass();
//        String jarPath = javaParameters.getJarPath();
//        if (mainClass != null) {
//            commandLine.addParameter(mainClass);
//        }
//        else if (jarPath != null) {
//            commandLine.addParameter("-jar");
//            commandLine.addParameter(jarPath);
//        }
//
//        commandLine.addParameters(javaParameters.getProgramParametersList().getList());
//
//        commandLine.withWorkDirectory(javaParameters.getWorkingDirectory());

        return commandLine;
    }

    @Nullable
    private static Class getCommandLineWrapperClass() {
        try {
            return Class.forName(WRAPPER_CLASS);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean useDynamicVMOptions() {
        return Boolean.valueOf(PropertiesComponent.getInstance().getOrInit("dynamic.vmoptions", "true")).booleanValue();
    }

    private static void appendEncoding(SimpleJavaParameters javaParameters, GeneralCommandLine commandLine, ParametersList parametersList) {
        // Value of file.encoding and charset of GeneralCommandLine should be in sync in order process's input and output be correctly handled.
        String encoding = parametersList.getPropertyValue("file.encoding");
        if (encoding == null) {
            Charset charset = javaParameters.getCharset();
            if (charset == null) charset = EncodingManager.getInstance().getDefaultCharset();
            commandLine.addParameter("-Dfile.encoding=" + charset.name());
            commandLine.withCharset(charset);
        }
        else {
            try {
                Charset charset = Charset.forName(encoding);
                commandLine.withCharset(charset);
            }
            catch (UnsupportedCharsetException ignore) { }
            catch (IllegalCharsetNameException ignore) { }
        }
    }

    private static void appendParamsEncodingClasspath(SimpleJavaParameters javaParameters,
                                                      GeneralCommandLine commandLine,
                                                      ParametersList parametersList) {
        commandLine.addParameters(parametersList.getList());
        appendEncoding(javaParameters, commandLine, parametersList);
        if (!parametersList.hasParameter("-classpath") && !parametersList.hasParameter("-cp") && !javaParameters.getClassPath().getPathList().isEmpty()){
            commandLine.addParameter("-classpath");
            commandLine.addParameter(javaParameters.getClassPath().getPathsString());
        }
    }

}
