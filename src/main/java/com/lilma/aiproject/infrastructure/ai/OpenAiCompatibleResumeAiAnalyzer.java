package com.lilma.aiproject.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lilma.aiproject.common.config.AiProperties;
import com.lilma.aiproject.common.exception.BusinessException;
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
    private RestTemplate restTemplate;
    @Autowired
    private AiProperties aiProperties;

    private final ObjectMapper objectMapper=new ObjectMapper();


    @Override
    public String analyze(String resumeText) {
        try{
            Map<String,Object> requestBody=new HashMap<>();
            requestBody.put("model",aiProperties.getModel());

            List<Map<String,String>> message=new ArrayList<>();

            Map<String,String> systemMessage=new HashMap<>();
            systemMessage.put("role","system");
            systemMessage
                    .put("content","你是一名专业的简历分析顾问。请基于客户提供的简历内容，输出结构化分析，至少包括：" +
                            "1.候选人背景概述；2.核心优势；3.存在的问题；4.适合的岗位方向；5.优化建议。回答要清晰、专业、分点");
            message.add(systemMessage);

            Map<String,String> userMessage=new HashMap<>();
            userMessage.put("role","user");
            userMessage.put("coontent","请分析这份简历：\n\n"+resumeText);
            message.add(userMessage);

            requestBody.put("message",message);
            requestBody.put("temperature",0.3);

            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiProperties.getApiKey());

            HttpEntity<Map<String,Object>> requestEntity=new HttpEntity<>(requestBody,headers);

            ResponseEntity<String> response=restTemplate.exchange(
                    aiProperties.getBaseUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            JsonNode root=objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        }catch(Exception e){
            throw new RuntimeException("AI简历分析失败："+e.getMessage(),e);
        }
    }
}
