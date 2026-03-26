package com.lilma.aiproject.modules.interview.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateInterviewSessionRequest {
    @NotNull(message = "resumeId不能为空")
    private Long resumeId;
}
