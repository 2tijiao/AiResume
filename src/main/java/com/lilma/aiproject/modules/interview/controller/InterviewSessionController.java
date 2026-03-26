package com.lilma.aiproject.modules.interview.controller;

import com.lilma.aiproject.common.api.ApiResponse;
import com.lilma.aiproject.modules.interview.dto.CreateInterviewSessionRequest;
import com.lilma.aiproject.modules.interview.dto.SubmitInterviewAnswerRequest;
import com.lilma.aiproject.modules.interview.service.InterviewSessionService;
import com.lilma.aiproject.modules.interview.vo.InterviewSessionDetailVO;
import com.lilma.aiproject.modules.interview.vo.InterviewSessionListItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interview-session")
public class InterviewSessionController {
    @Autowired
    private InterviewSessionService interviewSessionService;

    @PostMapping
    public ApiResponse<Map<String,Long>> createSession(@Valid @RequestBody CreateInterviewSessionRequest request){
        Long sessionId= interviewSessionService.createSession(request);
        return ApiResponse.success(Collections.singletonMap("sessionId",sessionId));
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<InterviewSessionDetailVO> getSessionDetail(@PathVariable Long sessionId){
        return ApiResponse.success(interviewSessionService.getSessionDetail(sessionId));
    }

    @PostMapping("/{sessionId}/answers")
    public ApiResponse<?> submitAnswer(@PathVariable Long sessionId, @Valid @RequestBody SubmitInterviewAnswerRequest request){
        interviewSessionService.submitAnswer(sessionId,request);
        return ApiResponse.success();
    }

    @GetMapping
    public ApiResponse<List<InterviewSessionListItemVO>> listSessions() {
        return ApiResponse.success(interviewSessionService.listSessions());
    }
}
