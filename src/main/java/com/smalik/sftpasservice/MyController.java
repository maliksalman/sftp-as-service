package com.smalik.sftpasservice;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MyController {

    private Logger logger = LoggerFactory.getLogger(MyController.class);
    private JschConnectionFactory connectionFactory;

    public MyController(JschConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @GetMapping("/files")
    public List<FileInfo> listFiles() {
        try (JschConnection connection = connectionFactory.createNewConnection()) {

            return (List<FileInfo>) connection.getChannel().ls("upload").stream().map(obj -> {

                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) obj;
                return new FileInfo(
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
        response.setContentType("application/octet-stream");
        try (JschConnection connection = connectionFactory.createNewConnection()) {
            connection.getChannel().get("upload/" + filename, response.getOutputStream());
        } catch(SftpException | IOException e) {
            logger.error("Something went wrong", e);
        }
    }

    @DeleteMapping("/files/{filename}")
    public boolean deleteFile(@PathVariable("filename") String filename) {
        try (JschConnection connection = connectionFactory.createNewConnection()) {
            connection.getChannel().rm("upload/" + filename);
            return true;
        } catch(SftpException e) {
            logger.error("Something went wrong", e);
            return false;
        }
    }

    @PutMapping("/files/{filename}")
    public boolean uploadFile(final @PathVariable("filename") String filename, final @RequestParam("file") MultipartFile file) {
        try (JschConnection connection = connectionFactory.createNewConnection()) {
            connection.getChannel().put(file.getInputStream(), "upload/" + filename);
            return true;
        } catch(SftpException | IOException e) {
            logger.error("Something went wrong", e);
            return false;
        }
    }
}
