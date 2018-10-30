package com.smalik.sftpasservice;

public class JschConnectionFactory {

    private String host;
    private int port;
    private String user;
    private String password;

    public JschConnectionFactory(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public JschConnection createNewConnection() {
        try {
            return new JschConnection(host, port, user, password);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
