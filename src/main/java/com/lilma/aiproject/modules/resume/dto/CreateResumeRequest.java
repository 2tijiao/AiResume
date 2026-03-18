package com.lilma.aiproject.modules.resume.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateResumeRequest {
    @NotBlank(message = "fileName不能为空")
    private String fileName;

    @NotBlank(message = "storageKey不能为空")
    private String storageKey;

    private String resumeText;
}
