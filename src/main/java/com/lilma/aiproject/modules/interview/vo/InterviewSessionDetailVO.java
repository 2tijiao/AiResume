package com.lilma.aiproject.modules.interview.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InterviewSessionDetailVO {
    private Long sessionId;
    private Long resumeId;
    private String status;
    private Integer questionCount;
    private Integer answeredCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer overallScore;
    private String summary;
    private String finalSuggestion;

    private List<QuestionItem> questions;
    private List<AnswerItem> answers;

    @Data
    public static class QuestionItem{
        private Long questionId;
        private Integer questionOrder;
        private String questionText;
        private String sourceType;
    }

    @Data
    public static class AnswerItem{
        private Long answerId;
        private Long questionId;
        private String answerText;
        private String status;
        private Integer score;
        private String feedback;
        private String improvementSuggestion;
    }

}
