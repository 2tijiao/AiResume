package com.lilma.aiproject.infrastructure.ai;

import com.lilma.aiproject.common.config.AiProperties;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenAiCompatibleInterviewAnswerEvaluator implements InterviewAnswerEvaluator{
        @Autowired
        private OpenAIClient openAIClient;
        @Autowired
        private AiProperties aiProperties;

        @Override
        public InterviewAnswerEvaluationResult evaluate(String questionText, String answerText) {
            try {
                ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                        .model(aiProperties.getModel())
                        .addSystemMessage(
                                "你是一名专业的中文模拟面试官。请根据给定的面试问题和候选人的回答，输出结构化点评。"
                                        + "请严格按以下格式输出：\n"
                                        + "score: 分数（0-100的整数）\n"
                                        + "feedback: 对回答的整体评价\n"
                                        + "suggestion: 给候选人的改进建议\n"
                                        + "不要输出多余解释。"
                        )
                        .addUserMessage("面试问题：\n" + questionText + "\n\n候选人回答：\n" + answerText)
                        .build();

                ChatCompletion completion = openAIClient.chat().completions().create(params);

                completion.choices();
                if (completion.choices().isEmpty()) {
                    throw new RuntimeException("模型没有返回有效choices");
                }

                String content = completion.choices().get(0).message().content().orElse(null);
                if (content == null || content.trim().isEmpty()) {
                    throw new RuntimeException("模型返回点评内容为空");
                }

                return parseEvaluation(content);

            } catch (Exception e) {
                throw new RuntimeException("AI点评回答失败: " + e.getMessage(), e);
            }
        }

        private InterviewAnswerEvaluationResult parseEvaluation(String content) {
            int score = 70;
            String feedback = null;
            String suggestion = null;

            String[] lines = content.split("\\r?\\n");
            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.startsWith("score:")) {
                    String value = line.substring("score:".length()).trim();
                    try {
                        score = Integer.parseInt(value.replaceAll("[^0-9]", ""));
                    } catch (Exception ignored) {
                    }
                } else if (line.startsWith("feedback:")) {
                    feedback = line.substring("feedback:".length()).trim();
                } else if (line.startsWith("suggestion:")) {
                    suggestion = line.substring("suggestion:".length()).trim();
                }
            }

            if (feedback == null || feedback.isEmpty()) {
                feedback = content;
            }
            if (suggestion == null || suggestion.isEmpty()) {
                suggestion = "建议进一步优化表达结构，突出关键经历、具体贡献和结果。";
            }

            return new InterviewAnswerEvaluationResult(score, feedback, suggestion);
        }
}
