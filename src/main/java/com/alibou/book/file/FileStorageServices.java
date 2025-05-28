package com.alibou.book.file;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServices {

    private final Path fileStorageLocation;

    public FileStorageServices() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Copy file to target location
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Return the file URL/path (modify as needed for your application)
        return "/uploads/" + fileName;
    }
}