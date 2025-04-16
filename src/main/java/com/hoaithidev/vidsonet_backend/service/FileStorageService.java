package com.hoaithidev.vidsonet_backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String directory);

    void deleteFile(String filePath);

    String getFileUrl(String fileName);
}
