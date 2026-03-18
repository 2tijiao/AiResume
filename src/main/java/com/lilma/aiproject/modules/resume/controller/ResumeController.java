package com.lilma.aiproject.modules.resume.controller;

import com.lilma.aiproject.common.api.ApiResponse;
import com.lilma.aiproject.modules.resume.dto.CreateResumeRequest;
import com.lilma.aiproject.modules.resume.entity.Resume;
import com.lilma.aiproject.modules.resume.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/resumes")
@Validated
public class ResumeController {
    @Autowired
    private ResumeService resumeService;

    @PostMapping
    public ApiResponse<Resume> create(@Valid @RequestBody CreateResumeRequest request) {
        return ApiResponse.success(resumeService.create(request));
    }

    @GetMapping
    public ApiResponse<List<Resume>> list() {
        return ApiResponse.success(resumeService.list());
    }

    @PostMapping("/upload")
    public ApiResponse<Resume> upload(@RequestParam("file")MultipartFile file){
        return ApiResponse.success(resumeService.upload(file));
    }

    @GetMapping("/{id}")
    public ApiResponse<Resume> getById(@PathVariable Long id){
        return ApiResponse.success(resumeService.getById(id));
    }

}
