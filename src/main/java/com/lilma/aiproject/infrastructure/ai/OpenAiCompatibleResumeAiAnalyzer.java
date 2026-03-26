package com.lilma.aiproject.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilma.aiproject.common.config.AiProperties;
import com.lilma.aiproject.common.exception.BusinessException;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiCompatibleResumeAiAnalyzer implements ResumeAiAnalyzer{
    @Autowired
    private OpenAIClient openAIClient;
    @Autowired
    private AiProperties aiProperties;

    @Override
    public String analyze(String resumeText) {
        try{
            ChatCompletionCreateParams params=ChatCompletionCreateParams.builder()
                    .model(aiProperties.getModel())
                    .addSystemMessage(
                            "你是一名专业的简历分析顾问。请基于客户提供的简历内容，输出结构化分析，至少包括：" +
                                    "1.候选人背景概述；2.核心优势；3.存在的问题；4.适合的岗位方向；5.优化建议。回答要清晰、专业、分点、中文输出。"
                    )
                    .addUserMessage("请分析这份简历：\n\n"+resumeText)
                    .build();

            ChatCompletion completion=openAIClient.chat().completions().create(params);
            //completion.choices();
            if(completion.choices().isEmpty()){
                throw new RuntimeException("模型没有返回有效choices");
            }

            String content=completion.choices().get(0).message().content().orElse(null);
            if(content==null||content.trim().isEmpty()){
                throw new RuntimeException("模型返回内容为空");
            }

            return content;
        }catch(Exception e){
            throw new RuntimeException("AI简历分析失败："+e.getMessage(),e);
        }
    }
}
