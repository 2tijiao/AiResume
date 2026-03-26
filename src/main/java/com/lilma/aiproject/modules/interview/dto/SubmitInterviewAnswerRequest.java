package com.lilma.aiproject.modules.interview.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubmitInterviewAnswerRequest {
    @NotNull(message = "questionId不能为空")
    private Long questionId;
    @NotNull(message = "answerText不能为空")
    private String answerText;
}
