package com.blocklatency.embeddedlinuxjvm.protocol.ssh.jsch;


import com.jcraft.jsch.UserInfo;
import lombok.Builder;

@Builder
public class EmbeddedUserInfo implements UserInfo {
    private String password;

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassword(String s) {
        return false;
    }

    @Override
    public boolean promptPassphrase(String s) {
        return false;
    }

    @Override
    public boolean promptYesNo(String s) {
        return false;
    }

    @Override
    public void showMessage(String s) {

    }
}
