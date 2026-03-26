package com.lilma.aiproject.modules.interview.service;

import com.lilma.aiproject.modules.interview.dto.CreateInterviewSessionRequest;
import com.lilma.aiproject.modules.interview.dto.SubmitInterviewAnswerRequest;
import com.lilma.aiproject.modules.interview.vo.InterviewSessionDetailVO;
import com.lilma.aiproject.modules.interview.vo.InterviewSessionListItemVO;

import java.util.List;

public interface InterviewSessionService {
    Long createSession(CreateInterviewSessionRequest request);

    InterviewSessionDetailVO getSessionDetail(Long sessionId);

    void submitAnswer(Long sessionId, SubmitInterviewAnswerRequest request);

    List<InterviewSessionListItemVO> listSessions();
}
