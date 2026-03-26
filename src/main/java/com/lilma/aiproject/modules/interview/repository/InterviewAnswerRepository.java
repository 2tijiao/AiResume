package com.lilma.aiproject.modules.interview.repository;

import com.lilma.aiproject.modules.interview.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer,Long> {
    List<InterviewAnswer> findBySessionId(Long sessionId);

    Optional<InterviewAnswer> findBySessionIdAndQuestionId(Long sessionId,Long questionId);

    long countBySessionIdAndStatus(Long sessionId,String status);

}
