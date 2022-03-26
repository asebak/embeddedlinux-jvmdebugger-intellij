package com.blocklatency.embeddedlinuxjvm.protocol.ssh.jsch;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import lombok.Builder;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Builder
public class EmbeddedSSHClient {
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String key;
    private boolean useKey;
    private String hostname;
    @NotNull
    private int port;

    /**
     * Gets SSH Client
     *
     * @return
     */
    @SneakyThrows(JSchException.class)
    public Session get() {
        JSch jsch = new JSch();
        UserInfo userInfo;
        Session session = jsch.getSession(username, hostname, port);
        if (useKey) {
            jsch.addIdentity(key);
            userInfo = new EmbeddedUserInfoInteractive();
        } else {
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            userInfo = EmbeddedUserInfo.builder().password(password).build();
        }
        session.setUserInfo(userInfo);
        session.setConfig("HashKnownHosts", "yes");
        session.setTimeout(10000);
        session.connect();
        return session;
    }
}
