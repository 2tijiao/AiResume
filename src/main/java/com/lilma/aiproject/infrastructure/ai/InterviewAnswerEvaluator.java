package com.lilma.aiproject.infrastructure.ai;

public interface InterviewAnswerEvaluator {
    InterviewAnswerEvaluationResult evaluate(String questionText,String answerText);
}
