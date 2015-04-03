package com.atsebak.raspberrypi.utils;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.util.DisposeAwareRunnable;


public class ProjectUtils {
    public static void runWhenInitialized(final Project project, final Runnable r) {
        if (project.isDisposed()) return;

        if (isNoBackgroundMode()) {
            r.run();
            return;
        }

        if (!project.isInitialized()) {
            StartupManager.getInstance(project).registerPostStartupActivity(DisposeAwareRunnable.create(r, project));
            return;
        }

        runDumbAware(project, r);
    }

    public static void runDumbAware(final Project project, final Runnable r) {
        if (DumbService.isDumbAware(r)) {
            r.run();
        } else {
            DumbService.getInstance(project).runWhenSmart(DisposeAwareRunnable.create(r, project));
        }
    }

    public static boolean isNoBackgroundMode() {
        return (ApplicationManager.getApplication().isUnitTestMode()
                || ApplicationManager.getApplication().isHeadlessEnvironment());
    }


    public static void addProjectConfiguration(final Module module, final Project project) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                final RunManager runManager = RunManager.getInstance(project);
                final RunnerAndConfigurationSettings settings = runManager.
                        createRunConfiguration(module.getName(), AndroidRunConfigurationType.getInstance().getFactory());
                final AndroidRunConfiguration configuration = (AndroidRunConfiguration) settings.getConfiguration();
                configuration.setModule(module);

                if (activityClass != null) {
                    configuration.MODE = AndroidRunConfiguration.LAUNCH_SPECIFIC_ACTIVITY;
                    configuration.ACTIVITY_CLASS = activityClass;
                } else if (AndroidRunConfiguration.isWatchFaceApp(facet)) {
                    // In case of a watch face app, there is only a service and no default activity that can be launched
                    // Eventually, we'd need to support launching a service, but currently you cannot launch a watch face service as well.
                    // See https://code.google.com/p/android/issues/detail?id=151353
                    configuration.MODE = AndroidRunConfiguration.DO_NOTHING;
                } else {
                    configuration.MODE = AndroidRunConfiguration.LAUNCH_DEFAULT_ACTIVITY;
                }

                if (targetSelectionMode != null) {
                    configuration.setTargetSelectionMode(targetSelectionMode);
                }
                if (preferredAvdName != null) {
                    configuration.PREFERRED_AVD = preferredAvdName;
                }
                runManager.addConfiguration(settings, false);
                runManager.setSelectedConfiguration(settings);
            }
        };
        if (!ask) {
            r.run();
        } else {
            UIUtil.invokeLaterIfNeeded(new Runnable() {
                @Override
                public void run() {
                    final String moduleName = facet.getModule().getName();
                    final int result = Messages.showYesNoDialog(project, AndroidBundle.message("create.run.configuration.question", moduleName),
                            AndroidBundle.message("create.run.configuration.title"), Messages.getQuestionIcon());
                    if (result == Messages.YES) {
                        r.run();
                    }
                }
            });
        }
    }
}
