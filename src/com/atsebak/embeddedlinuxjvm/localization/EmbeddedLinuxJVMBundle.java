/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atsebak.embeddedlinuxjvm.localization;

import com.intellij.BundleBase;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * The bundle for PI messages
 */
public class EmbeddedLinuxJVMBundle {

    @NonNls
    private static final String BUNDLE = "com.atsebak.embeddedlinuxjvm.localization.EmbeddedLinuxJVMBundle";
    private static Reference<ResourceBundle> ourBundle;

    /**
     * Private Constructor
     */
    private EmbeddedLinuxJVMBundle() {
    }

    /**
     * @param key
     * @param params
     * @return
     */
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
        return BundleBase.message(getBundle(), key, params);
    }

    /**
     * @return
     */
    private static ResourceBundle getBundle() {
        ResourceBundle bundle = com.intellij.reference.SoftReference.dereference(ourBundle);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            ourBundle = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }

    /**
     * @param key The Key
     * @return The Localized Text
     */
    public static String getString(@PropertyKey(resourceBundle = BUNDLE) final String key) {
        return getBundle().getString(key);
    }
}
