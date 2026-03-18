package com.lilma.aiproject.modules.resume.repository;

import com.lilma.aiproject.modules.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume,Long> {
    //返回optional避免空指针问题
    Optional<Resume> findByContentHash(String contentHash);
}
