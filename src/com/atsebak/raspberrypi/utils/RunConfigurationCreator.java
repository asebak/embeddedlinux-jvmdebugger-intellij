package com.atsebak.raspberrypi.utils;

/**
 * Created by asebak on 03/04/15.
 */
public class RunConfigurationCreator {
    public static void add() {
//        Project project;
//        final Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                final RunManager runManager = RunManager.getInstance(project);
//                final RunnerAndConfigurationSettings settings = runManager.
//                        createRunConfiguration(module.getName(), AndroidRunConfigurationType.getInstance().getFactory());
//                final AndroidRunConfiguration configuration = (AndroidRunConfiguration)settings.getConfiguration();
//                configuration.setModule(module);
//
//                if (activityClass != null) {
//                    configuration.MODE = AndroidRunConfiguration.LAUNCH_SPECIFIC_ACTIVITY;
//                    configuration.ACTIVITY_CLASS = activityClass;
//                }
//                else if (AndroidRunConfiguration.isWatchFaceApp(facet)) {
//                    // In case of a watch face app, there is only a service and no default activity that can be launched
//                    // Eventually, we'd need to support launching a service, but currently you cannot launch a watch face service as well.
//                    // See https://code.google.com/p/android/issues/detail?id=151353
//                    configuration.MODE = AndroidRunConfiguration.DO_NOTHING;
//                }
//                else {
//                    configuration.MODE = AndroidRunConfiguration.LAUNCH_DEFAULT_ACTIVITY;
//                }
//
//                if (targetSelectionMode != null) {
//                    configuration.setTargetSelectionMode(targetSelectionMode);
//                }
//                if (preferredAvdName != null) {
//                    configuration.PREFERRED_AVD = preferredAvdName;
//                }
//                runManager.addConfiguration(settings, false);
//                runManager.setSelectedConfiguration(settings);
//            }
//        };
//        if (!ask) {
//            r.run();
//        }
//        else {
//            UIUtil.invokeLaterIfNeeded(new Runnable() {
//                @Override
//                public void run() {
//                    final String moduleName = facet.getModule().getName();
//                    final int result = Messages.showYesNoDialog(project, AndroidBundle.message("create.run.configuration.question", moduleName),
//                            AndroidBundle.message("create.run.configuration.title"), Messages.getQuestionIcon());
//                    if (result == Messages.YES) {
//                        r.run();
//                    }
//                }
//            });
//        }
    }
}
