package com.lilma.aiproject.infrastructure.ai;

import java.util.List;

public interface InterviewQuestionGenerator {
    List<String> generatorQuestions(String resumeText);
}
