package com.lilma.aiproject.modules.resume.entity;

import com.lilma.aiproject.common.constant.ResumeStatusConstants;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="resume")
public class Resume {
    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //文件名称
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false, length = 512)
    private String storageKey;

    @Column(length = 1024)
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String resumeText;

    //简历内容哈希，用于相同简历查重
    @Column(length = 64,unique = true)
    private String contentHash;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String analysisResult;

    @Column(columnDefinition = "TEXT")
    private String failReason;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = ResumeStatusConstants.PENDING;
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
