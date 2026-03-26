package com.lilma.aiproject.infrastructure.ai;

import com.lilma.aiproject.common.config.AiProperties;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiCompatibleInterviewQuestionGenerator implements InterviewQuestionGenerator{
    @Autowired
    private OpenAIClient openAiClient;
    @Autowired
    private AiProperties aiProperties;

    @Override
    public List<String> generatorQuestions(String resumeText) {
        try{
            ChatCompletionCreateParams params=ChatCompletionCreateParams.builder()
                    .model(aiProperties.getModel())
                    .addSystemMessage("你是一名专业的中文模拟面试官。请基于候选人的简历内容，生成3道适合模拟面试的问题。"
                            + "要求："
                            + "1. 问题必须具体，尽量贴近候选人的经历；"
                            + "2. 覆盖自我介绍/经历深挖/优势或反思类问题；"
                            + "3. 只输出问题本身，每行一道，不要加编号解释，不要输出多余文字。")
                    .addUserMessage("请基于这份简历生成3道面试题：\n\n" + resumeText)
                    .build();

            ChatCompletion completion = openAiClient.chat().completions().create(params);
            completion.choices();
            if(completion.choices().isEmpty())throw new RuntimeException("模型没有返回有效的choices");

            String content=completion.choices().get(0).message().content().orElse(null);
            if(content==null||content.trim().isEmpty())throw new RuntimeException("模型返回题目内容为空");

            return parseQuestions(content);
        }catch (Exception e){
            throw new RuntimeException("AI生成面试题失败："+e.getMessage(),e);
        }
    }

    //数据清洗
    private List<String> parseQuestions(String content){
        String[] lines=content.split("\\r?\\n");
        List<String> result=new ArrayList<>();

        for(String line:lines){
            String text=line.trim();
            if(text.isEmpty())continue;

            text = text.replaceFirst("^\\d+[\\.、\\s]*", "");
            text = text.replaceFirst("^[-•]\\s*", "");

            if(!text.isEmpty())result.add(text);

            if(result.size()>=3)break;
        }

        if(result.size()<3)throw new RuntimeException("AI生成的题目数量不足，原始内容："+content);

        return result;
    }
}
