package com.lilma.aiproject.infrastructure.ai;

import java.util.List;

public interface InterviewSessionSummarizer {
    InterviewSessionSummaryResult summarize(
            List<String> questions,
            List<String> answer,
            List<String> feedbacks
    );
}
