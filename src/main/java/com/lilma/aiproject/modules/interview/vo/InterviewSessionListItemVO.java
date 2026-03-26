package com.lilma.aiproject.modules.interview.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewSessionListItemVO {

    private Long sessionId;
    private Long resumeId;
    private String status;
    private Integer questionCount;
    private Integer answeredCount;
    private Integer overallScore;
    private String summaryPreview;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
