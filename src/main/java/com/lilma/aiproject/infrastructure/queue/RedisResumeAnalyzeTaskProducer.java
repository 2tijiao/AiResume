package com.lilma.aiproject.infrastructure.queue;

import com.lilma.aiproject.common.constant.RedisQueueConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisResumeAnalyzeTaskProducer implements ResumeAnalyzeTaskProducer{
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void send(Long resumeId) {
        stringRedisTemplate.opsForList().rightPush(
                RedisQueueConstants.RESUME_ANALYZE_QUEUE,
                String.valueOf(resumeId)
        );
    }
}
