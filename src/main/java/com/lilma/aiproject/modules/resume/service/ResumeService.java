package com.lilma.aiproject.modules.resume.service;

import com.lilma.aiproject.modules.resume.dto.CreateResumeRequest;
import com.lilma.aiproject.modules.resume.entity.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {
    Resume create(CreateResumeRequest request);

    List<Resume> list();

    Resume upload(MultipartFile file);

    Resume getById(Long id);

    Resume analyze(Long id);
}
