package com.smalik.sftpasservice;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SftpController {

    private Logger logger = LoggerFactory.getLogger(SftpController.class);

    private SftpConnectionFactory connectionFactory;
    private String defaultDir;

    public SftpController(SftpConnectionFactory connectionFactory, @Value("${sftp.defaultDir:upload}") String defaultDir) {
        this.connectionFactory = connectionFactory;
        this.defaultDir = defaultDir;
    }

    @GetMapping("/files")
    public List<SftpFileInfo> listFiles() {
        try (SftpConnection connection = connectionFactory.createNewConnection()) {

            return (List<SftpFileInfo>) connection.getChannel().ls(defaultDir).stream().map(obj -> {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) obj;
                return new SftpFileInfo(
                        entry.getFilename(),
                        new Date(entry.getAttrs().getMTime() * 1000L),
                        entry.getAttrs().isDir(),
                        entry.getAttrs().getSize());

            }).collect(Collectors.toList());

        } catch(SftpException e) {
            logger.error("Something went wrong", e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/files/{filename}")
    public void downloadFile(@PathVariable("filename") String filename, HttpServletResponse response) {
        try (SftpConnection connection = connectionFactory.createNewConnection()) {

            response.setContentType("application/octet-stream");
            connection.getChannel().get(defaultDir + "/" + filename, response.getOutputStream());

        } catch(SftpException | IOException e) {
            logger.error("Something went wrong", e);
        }
    }

    @DeleteMapping("/files/{filename}")
    public boolean deleteFile(@PathVariable("filename") String filename) {
        try (SftpConnection connection = connectionFactory.createNewConnection()) {

            connection.getChannel().rm(defaultDir + "/" + filename);
            return true;

        } catch(SftpException e) {
            logger.error("Something went wrong", e);
            return false;
        }
    }

    @PutMapping("/files/{filename}")
    public boolean uploadFile(@PathVariable("filename") String filename, @RequestParam("file") MultipartFile file) {
        try (SftpConnection connection = connectionFactory.createNewConnection()) {

            connection.getChannel().put(file.getInputStream(), defaultDir + "/" + filename);
            return true;

        } catch(SftpException | IOException e) {
            logger.error("Something went wrong", e);
            return false;
        }
    }
}
