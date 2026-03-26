package com.lilma.aiproject.infrastructure.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewAnswerEvaluationResult {
    private Integer score;
    private String feedback;
    private String improvementSuggestion;
}
