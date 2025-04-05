package com.projectX.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        String originalFile = file.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFile.substring(originalFile.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;
        File folder = new File(path);

        if(!folder.exists()) folder.mkdir();
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {

        String filePath = path + File.separator + fileName;
        InputStream ips = new FileInputStream(filePath);
        return ips;

    }
}
