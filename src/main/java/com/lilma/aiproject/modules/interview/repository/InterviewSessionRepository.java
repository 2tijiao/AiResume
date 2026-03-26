package com.lilma.aiproject.modules.interview.repository;

import com.lilma.aiproject.modules.interview.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession,Long> {
}
