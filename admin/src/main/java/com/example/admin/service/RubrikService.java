package com.example.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class RubrikService {
    
    // Define where to store uploaded files
    private final Path uploadDir = Paths.get("src/main/resources/static/uploads/rubrik");
    
    public RubrikService() {
        try {
            // Create directory if it doesn't exist
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }
    
    public String storeFile(MultipartFile file, Integer idTubes) throws IOException {
        // Generate filename
        // Removed the underscore before extension and fixed extension handling
        String filename = "rubrik_" + idTubes + ".pdf";
                
        // Copy file to upload directory
        Path targetLocation = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Return the relative path for web access
        return "/uploads/rubrik/" + filename;
    }
    
    public boolean deleteFile(String filePath) {
        try {
            // Remove leading slash and convert to Path
            String relativePath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
            Path fileToDelete = Paths.get("src/main/resources/static", relativePath);
            
            return Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            return false;
        }
    }
}