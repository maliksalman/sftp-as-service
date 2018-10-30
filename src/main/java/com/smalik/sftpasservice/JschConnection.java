package com.smalik.sftpasservice;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.Closeable;

public class JschConnection implements Closeable {

    private Session session;
    private ChannelSftp channel;

    public JschConnection(String host, int port, String user, String password) throws Exception {
        JSch jSch = new JSch();
        session = jSch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();

        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }

    public ChannelSftp getChannel() {
        return channel;
    }

    @Override
    public void close() {
        channel.exit();
        session.disconnect();
    }
}
