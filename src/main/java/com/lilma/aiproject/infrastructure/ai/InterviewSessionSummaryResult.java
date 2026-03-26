package com.lilma.aiproject.infrastructure.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSessionSummaryResult {

    private Integer overallScore;
    private String summary;
    private String finalSuggestion;
}
