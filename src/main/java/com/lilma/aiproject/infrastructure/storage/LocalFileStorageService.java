package com.lilma.aiproject.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface LocalFileStorageService {
    String save(MultipartFile file);

    void delete(String filePath);
}
