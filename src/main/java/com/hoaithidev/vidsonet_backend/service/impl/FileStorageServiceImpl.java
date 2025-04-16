package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.exception.ErrorCode;
import com.hoaithidev.vidsonet_backend.exception.VidsonetException;
import com.hoaithidev.vidsonet_backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Override
    public String storeFile(MultipartFile file, String directory) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Check if the file's name contains invalid characters
        if (originalFileName.contains("..")) {
            throw new VidsonetException(ErrorCode.INVALID_REQUEST, "Filename contains invalid path sequence " + originalFileName);
        }

        // Generate unique file name
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // Create target directory if it doesn't exist
        Path targetDir = Paths.get(uploadDir, directory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(targetDir);

            // Copy file to the target location
            Path targetLocation = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return directory + "/" + fileName;
        } catch (IOException ex) {
            throw new VidsonetException(ErrorCode.VIDEO_UPLOAD_FAILED, "Could not store file " + fileName);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path targetPath = Paths.get(uploadDir, filePath).toAbsolutePath().normalize();
            Files.deleteIfExists(targetPath);
        } catch (IOException ex) {
            // Log the error but don't throw exception as this is not critical
            System.err.println("Error deleting file: " + filePath);
        }
    }

    @Override
    public String getFileUrl(String fileName) {

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();
    }
}
