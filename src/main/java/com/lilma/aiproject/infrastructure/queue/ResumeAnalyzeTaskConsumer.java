package com.lilma.aiproject.infrastructure.queue;

import com.lilma.aiproject.common.constant.RedisQueueConstants;
import com.lilma.aiproject.common.constant.ResumeStatusConstants;
import com.lilma.aiproject.infrastructure.ai.ResumeAiAnalyzer;
import com.lilma.aiproject.modules.resume.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResumeAnalyzeTaskConsumer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private ResumeAiAnalyzer resumeAiAnalyzer;

    @Scheduled(fixedDelay = 3000)
    public void consume(){
        String resumeIdStr = stringRedisTemplate.opsForList().leftPop(RedisQueueConstants.RESUME_ANALYZE_QUEUE);
        if(resumeIdStr==null)return;

        Long resumeId=Long.valueOf(resumeIdStr);
        resumeRepository.findById(resumeId).ifPresent(resume -> {
            //为什么用try-catch包裹分析过程：即使某个任务失败消费者不也能崩溃
            try{
                resume.setStatus(ResumeStatusConstants.PROCESSING);
                resumeRepository.save(resume);

                String analyzeResult = resumeAiAnalyzer.analyze(resume.getResumeText());

                resume.setStatus(ResumeStatusConstants.COMPLETED);
                resume.setAnalysisResult(analyzeResult);
                resume.setFailReason(null);
                resumeRepository.save(resume);

            }catch (Exception e){
                resume.setStatus(ResumeStatusConstants.FAILED);
                resume.setFailReason(e.getMessage());

                //记录失败次数，有利于判断系统稳定性以及人工介入等
                Integer retryCount=resume.getRetryCount();
                if(retryCount==null)retryCount=0;
                resume.setRetryCount(retryCount+1);

                resumeRepository.save(resume);
            }
        });
    }
}
