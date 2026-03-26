package com.lilma.aiproject.modules.resume.controller;

import com.lilma.aiproject.common.api.ApiResponse;
import com.lilma.aiproject.modules.resume.dto.CreateResumeRequest;
import com.lilma.aiproject.modules.resume.entity.Resume;
import com.lilma.aiproject.modules.resume.service.ResumeService;
import com.lilma.aiproject.modules.resume.vo.ResumeListItemVO;
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
    public ApiResponse<List<ResumeListItemVO>> list() {
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

    @PostMapping("/{id}/analyze")
    public ApiResponse<?> analyze(@PathVariable Long id){
        resumeService.analyze(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id){
        resumeService.delete(id);
        return ApiResponse.success();
    }

    //重新分析接口，语义更清晰
    @PostMapping("/{id}/reanalyze")
    public ApiResponse<?> reanalyze(@PathVariable Long id){
        resumeService.analyze(id);
        return ApiResponse.success();
    }

}
