package com.lilma.aiproject.modules.interview.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.lilma.aiproject.common.constant.InterviewAnswerStatusConstants;
import com.lilma.aiproject.common.constant.InterviewSessionStatusConstants;
import com.lilma.aiproject.common.exception.BusinessException;
import com.lilma.aiproject.infrastructure.ai.*;
import com.lilma.aiproject.modules.interview.dto.CreateInterviewSessionRequest;
import com.lilma.aiproject.modules.interview.dto.SubmitInterviewAnswerRequest;
import com.lilma.aiproject.modules.interview.entity.InterviewAnswer;
import com.lilma.aiproject.modules.interview.entity.InterviewQuestion;
import com.lilma.aiproject.modules.interview.entity.InterviewSession;
import com.lilma.aiproject.modules.interview.repository.InterviewAnswerRepository;
import com.lilma.aiproject.modules.interview.repository.InterviewQuestionRepository;
import com.lilma.aiproject.modules.interview.repository.InterviewSessionRepository;
import com.lilma.aiproject.modules.interview.service.InterviewSessionService;
import com.lilma.aiproject.modules.interview.vo.InterviewSessionDetailVO;
import com.lilma.aiproject.modules.interview.vo.InterviewSessionListItemVO;
import com.lilma.aiproject.modules.resume.entity.Resume;
import com.lilma.aiproject.modules.resume.repository.ResumeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InterviewSessionServiceImpl implements InterviewSessionService {
    @Autowired
    private InterviewSessionRepository interviewSessionRepository;
    @Autowired
    private InterviewQuestionRepository interviewQuestionRepository;
    @Autowired
    private InterviewAnswerRepository interviewAnswerRepository;
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private InterviewQuestionGenerator interviewQuestionGenerator;
    @Autowired
    private InterviewAnswerEvaluator interviewAnswerEvaluator;
    @Autowired
    private InterviewSessionSummarizer interviewSessionSummarizer;

    @Override
    public Long createSession(CreateInterviewSessionRequest request) {
        Resume resume = resumeRepository.findById(request.getResumeId()).orElseThrow(() -> new BusinessException("简历不存在，resumeId=" + request.getResumeId()));
        if(resume.getResumeText()==null||resume.getResumeText().trim().isEmpty())throw new BusinessException("简历内容为空，无法生成面试题，resumeId="+request.getResumeId());

        List<String> generatedQuestions;
        String sourceType;
        try{
            generatedQuestions=interviewQuestionGenerator.generatorQuestions(resume.getResumeText());
            sourceType="AI";
        }catch (Exception e){
            generatedQuestions=buildDefaultQuestions();
            sourceType="SYSTEM";
        }

        InterviewSession session=new InterviewSession();
        session.setResumeId(request.getResumeId());
        session.setStatus("CREATED");
        session.setQuestionCount(generatedQuestions.size());
        session.setAnsweredCount(0);
        InterviewSession savedSession=interviewSessionRepository.save(session);

        List<InterviewQuestion> questions=new ArrayList<>();
        for(int i=0;i<generatedQuestions.size();i++){
            InterviewQuestion question=new InterviewQuestion();
            question.setSessionId(savedSession.getId());
            question.setQuestionOrder(i+1);
            question.setQuestionText(generatedQuestions.get(i));
            question.setSourceType(sourceType);
            questions.add(question);
        }

        interviewQuestionRepository.saveAll(questions);

        return savedSession.getId();
    }

    //兜底方法，当AI生成有误时，使用固定的问题
    private List<String> buildDefaultQuestions() {
        List<String> defaults = new ArrayList<>();
        defaults.add("请先做一个简短的自我介绍，并突出与你目标岗位最相关的经历。");
        defaults.add("请介绍一段你最有代表性的项目或实习经历，并说明你的具体贡献。");
        defaults.add("如果让你总结自己的三项核心优势，你会如何回答？");
        return defaults;
    }

    @Override
    public InterviewSessionDetailVO getSessionDetail(Long sessionId) {
        InterviewSession session = interviewSessionRepository.findById(sessionId).orElseThrow(() -> new BusinessException("面试会话不存在，sessionId=" + sessionId));

        List<InterviewQuestion> questionList=interviewQuestionRepository.findBySessionIdOrderByQuestionOrderAsc(sessionId);
        List<InterviewAnswer> answerList=interviewAnswerRepository.findBySessionId(sessionId);

        InterviewSessionDetailVO vo=new InterviewSessionDetailVO();
        vo.setSessionId(session.getId());
        vo.setResumeId(session.getResumeId());
        vo.setStatus(session.getStatus());
        vo.setQuestionCount(session.getQuestionCount());
        vo.setAnsweredCount(session.getAnsweredCount());
        vo.setCreatedAt(session.getCreatedAt());
        vo.setUpdatedAt(session.getUpdatedAt());
        vo.setOverallScore(session.getOverallScore());
        vo.setSummary(session.getSummary());
        vo.setFinalSuggestion(session.getFinalSuggestion());
        //BeanUtils.copyProperties(session,vo);

        List<InterviewSessionDetailVO.QuestionItem> questions=new ArrayList<>();
        for(InterviewQuestion question:questionList){
            InterviewSessionDetailVO.QuestionItem item = new InterviewSessionDetailVO.QuestionItem();
            item.setQuestionId(question.getId());
            item.setQuestionOrder(question.getQuestionOrder());
            item.setQuestionText(question.getQuestionText());
            item.setSourceType(question.getSourceType());
            questions.add(item);
        }
        vo.setQuestions(questions);

        List<InterviewSessionDetailVO.AnswerItem> answers = new ArrayList<>();
        for (InterviewAnswer answer : answerList) {
            InterviewSessionDetailVO.AnswerItem item = new InterviewSessionDetailVO.AnswerItem();
            item.setAnswerId(answer.getId());
            item.setQuestionId(answer.getQuestionId());
            item.setAnswerText(answer.getAnswerText());
            item.setStatus(answer.getStatus());
            answers.add(item);
        }
        vo.setAnswers(answers);

        return vo;

    }

    @Override
    public void submitAnswer(Long sessionId, SubmitInterviewAnswerRequest request) {
        InterviewSession session = interviewSessionRepository.findById(sessionId).orElseThrow(() -> new BusinessException("面试会话不存在，sessionId=" + sessionId));

        InterviewQuestion question = interviewQuestionRepository.findById(request.getQuestionId()).orElseThrow(() -> new BusinessException("面试问题不存在，questionId=" + request.getQuestionId()));

        if(!sessionId.equals(question.getSessionId())){
            throw new BusinessException("该问题不属于当前面试会话，questionId="+request.getQuestionId());
        }

        InterviewAnswer answer = interviewAnswerRepository.findBySessionIdAndQuestionId(sessionId, request.getQuestionId()).orElseGet(InterviewAnswer::new);
        answer.setSessionId(sessionId);
        answer.setQuestionId(request.getQuestionId());
        answer.setAnswerText(request.getAnswerText());
        answer.setStatus(InterviewAnswerStatusConstants.ANSWERED);

        InterviewAnswerEvaluationResult evaluationResult=interviewAnswerEvaluator.evaluate(question.getQuestionText(), request.getAnswerText());
        answer.setScore(evaluationResult.getScore());
        answer.setFeedback(evaluationResult.getFeedback());
        answer.setImprovementSuggestion(evaluationResult.getImprovementSuggestion());
        answer.setStatus(InterviewAnswerStatusConstants.EVALUATED);

        interviewAnswerRepository.save(answer);

        long answeredCount=interviewAnswerRepository.countBySessionIdAndStatus(sessionId,InterviewAnswerStatusConstants.ANSWERED);

        session.setAnsweredCount((int) answeredCount);

        if (answeredCount >= session.getQuestionCount()) {
            session.setStatus(InterviewSessionStatusConstants.COMPLETED);

            List<InterviewQuestion> questionList = interviewQuestionRepository.findBySessionIdOrderByQuestionOrderAsc(sessionId);
            List<InterviewAnswer> answerList = interviewAnswerRepository.findBySessionId(sessionId);

            List<String> questions = questionList.stream()
                    .map(InterviewQuestion::getQuestionText)
                    .collect(Collectors.toList());

            List<String> answers = answerList.stream()
                    .map(InterviewAnswer::getAnswerText)
                    .collect(Collectors.toList());

            List<String> feedbacks = answerList.stream()
                    .map(InterviewAnswer::getFeedback)
                    .collect(Collectors.toList());

            InterviewSessionSummaryResult summaryResult = interviewSessionSummarizer.summarize(
                    questions, answers, feedbacks
            );

            session.setOverallScore(summaryResult.getOverallScore());
            session.setSummary(summaryResult.getSummary());
            session.setFinalSuggestion(summaryResult.getFinalSuggestion());
        } else if (answeredCount > 0) {
            session.setStatus(InterviewSessionStatusConstants.IN_PROGRESS);
        }

        interviewSessionRepository.save(session);
    }

    @Override
    public List<InterviewSessionListItemVO> listSessions() {
        return interviewSessionRepository.findAll()
                .stream()
                .map(this::toSessionListItemVO)
                .collect(Collectors.toList());
    }

    private InterviewSessionListItemVO toSessionListItemVO(InterviewSession session) {
        InterviewSessionListItemVO vo = new InterviewSessionListItemVO();
        vo.setSessionId(session.getId());
        vo.setResumeId(session.getResumeId());
        vo.setStatus(session.getStatus());
        vo.setQuestionCount(session.getQuestionCount());
        vo.setAnsweredCount(session.getAnsweredCount());
        vo.setOverallScore(session.getOverallScore());
        vo.setSummaryPreview(buildPreview(session.getSummary(), 120));
        vo.setCreatedAt(session.getCreatedAt());
        vo.setUpdatedAt(session.getUpdatedAt());
        return vo;
    }

    private String buildPreview(String text, int maxLength) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String normalized = text.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, maxLength) + "...";
    }
}
