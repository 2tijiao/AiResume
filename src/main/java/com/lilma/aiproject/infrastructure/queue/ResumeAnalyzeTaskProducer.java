package com.lilma.aiproject.infrastructure.queue;

public interface ResumeAnalyzeTaskProducer {
    void send(Long resumeId);
}
