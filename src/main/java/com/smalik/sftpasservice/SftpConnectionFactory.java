package com.smalik.sftpasservice;

public class SftpConnectionFactory {

    private String host;
    private int port;
    private String user;
    private String password;

    public SftpConnectionFactory(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public SftpConnection createNewConnection() {
        try {
            return new SftpConnection(host, port, user, password);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
