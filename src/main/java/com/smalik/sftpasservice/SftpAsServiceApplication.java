package com.smalik.sftpasservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SftpAsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SftpAsServiceApplication.class, args);
	}

	@Bean
    public JschConnectionFactory createFactory(
            @Value("${sftp.host:127.0.0.1}") String host,
            @Value("${sftp.port:2222}") int port,
            @Value("${sftp.user:foo}") String user,
            @Value("${sftp.password:pass}") String password) {
	    return new JschConnectionFactory(host, port, user, password);
    }
}
