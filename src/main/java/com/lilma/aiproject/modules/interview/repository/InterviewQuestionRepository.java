package com.lilma.aiproject.modules.interview.repository;

import com.lilma.aiproject.modules.interview.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion,Long> {
    List<InterviewQuestion> findBySessionIdOrderByQuestionOrderAsc(Long sessionId);
}
