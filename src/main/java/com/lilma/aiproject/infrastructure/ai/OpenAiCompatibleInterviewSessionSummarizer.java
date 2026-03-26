package com.lilma.aiproject.infrastructure.ai;

import com.lilma.aiproject.common.config.AiProperties;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAiCompatibleInterviewSessionSummarizer implements InterviewSessionSummarizer{
    @Autowired
    private OpenAIClient openAIClient;
    @Autowired
    private AiProperties aiProperties;


    @Override
    public InterviewSessionSummaryResult summarize(List<String> questions, List<String> answer, List<String> feedbacks) {
        try{
            StringBuilder contentBuilder=new StringBuilder();
            for(int i=0;i<questions.size();i++){
                contentBuilder.append("问题").append(i+1).append(": ").append(questions.get(i)).append("\n");
                contentBuilder.append("回答").append(i+1).append(": ").append(i<answer.size()?answer.get(i):"").append("\n");
                contentBuilder.append("点评").append(i+1).append(": ").append(i<feedbacks.size()?feedbacks.get(i):"").append("\n\n");
            }
            ChatCompletionCreateParams params=ChatCompletionCreateParams.builder()
                    .model(aiProperties.getModel())
                    .addSystemMessage("你是一名专业的中文模拟面试官。请基于整场面试问答内容和每题点评，输出结构化总结。"
                            + "请严格按以下格式输出：\n"
                            + "overallScore: 分数（0-100的整数）\n"
                            + "summary: 对候选人整场表现的综合评价\n"
                            + "finalSuggestion: 对候选人的总体改进建议\n"
                            + "不要输出多余解释。")
                    .addUserMessage("以下是一场模拟面试的完整内容：\n\n" + contentBuilder)
                    .build();
            ChatCompletion completion=openAIClient.chat().completions().create(params);
            completion.choices();
            if(completion.choices().isEmpty())throw new RuntimeException("模型没有返回有效的choices");
            String content=completion.choices().get(0).message().content().orElse(null);
            if(content==null||content.trim().isEmpty())throw new RuntimeException("模型返回面试总结内容为空");
            return parseSummary(content);
        }catch (Exception e){
            throw new RuntimeException("AI生成面试总结失败："+e.getMessage(),e);
        }
    }

    private InterviewSessionSummaryResult parseSummary(String content){
        Integer overallScore = 75;
        String summary = null;
        String finalSuggestion = null;

        String[] lines = content.split("\\r?\\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.startsWith("overallScore:")) {
                String value = line.substring("overallScore:".length()).trim();
                try {
                    overallScore = Integer.parseInt(value.replaceAll("[^0-9]", ""));
                } catch (Exception ignored) {
                }
            } else if (line.startsWith("summary:")) {
                summary = line.substring("summary:".length()).trim();
            } else if (line.startsWith("finalSuggestion:")) {
                finalSuggestion = line.substring("finalSuggestion:".length()).trim();
            }
        }

        if (summary == null || summary.isEmpty()) {
            summary = content;
        }
        if (finalSuggestion == null || finalSuggestion.isEmpty()) {
            finalSuggestion = "建议继续提升回答结构化程度，增强案例细节、数据支撑和反思深度。";
        }

        return new InterviewSessionSummaryResult(overallScore, summary, finalSuggestion);
    }
}
