package com.lilma.aiproject.modules.resume.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResumeListItemVO {
    private Long id;
    private String fileName;
    private String status;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String resumeTextPreview;
    private String analysisResultPreview;
}
