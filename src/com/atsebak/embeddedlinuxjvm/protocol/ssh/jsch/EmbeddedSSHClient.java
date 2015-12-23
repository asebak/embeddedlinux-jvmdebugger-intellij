package com.atsebak.embeddedlinuxjvm.protocol.ssh.jsch;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import lombok.Builder;
import lombok.SneakyThrows;

@Builder
public class EmbeddedSSHClient {
    private static final int SSH_PORT = 22;
    private String username;
    private String password;
    private String hostname;


    /**
     * Gets SSH Client
     *
     * @return
     */
    @SneakyThrows(JSchException.class)
    public Session get() {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, SSH_PORT);
        session.setPassword(password);
        UserInfo userInfo = EmbeddedUserInfo.builder().password(password).build();
        session.setUserInfo(userInfo);
        session.setConfig("HashKnownHosts", "yes");
        session.setConfig("StrictHostKeyChecking", "no");
        session.setTimeout(10000);
        session.connect();
        return session;
    }
}
