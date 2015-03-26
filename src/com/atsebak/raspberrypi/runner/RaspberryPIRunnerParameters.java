package com.atsebak.raspberrypi.runner;

import com.intellij.ide.browsers.WebBrowser;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by asebak on 25/03/15.
 */
public class RaspberryPIRunnerParameters implements Cloneable {
    private String myUrl = "";
    private WebBrowser myNonDefaultBrowser;
    @Attribute("web_path")
    public String getUrl() {
        return myUrl;
    }
    public void setUrl(@NotNull String url) {
        myUrl = url;
    }
    @Transient
    @Nullable
    public WebBrowser getNonDefaultBrowser() {
        return myNonDefaultBrowser;
    }
    public void setNonDefaultBrowser(@Nullable WebBrowser nonDefaultBrowser) {
        myNonDefaultBrowser = nonDefaultBrowser;
    }
    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    protected RaspberryPIRunnerParameters clone() {
        try {
            return (RaspberryPIRunnerParameters) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
