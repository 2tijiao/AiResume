package com.lilma.aiproject.modules.resume.service.impl;

import com.lilma.aiproject.common.exception.BusinessException;
import com.lilma.aiproject.common.util.HashUtils;
import com.lilma.aiproject.infrastructure.ai.ResumeAiAnalyzer;
import com.lilma.aiproject.infrastructure.parser.DocumentParser;
import com.lilma.aiproject.infrastructure.parser.TikaDocumentParser;
import com.lilma.aiproject.infrastructure.storage.LocalFileStorageService;
import com.lilma.aiproject.modules.resume.dto.CreateResumeRequest;
import com.lilma.aiproject.modules.resume.entity.Resume;
import com.lilma.aiproject.modules.resume.repository.ResumeRepository;
import com.lilma.aiproject.modules.resume.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private LocalFileStorageService localFileStorageService;
    @Autowired
    private DocumentParser documentParser;
    @Autowired
    private ResumeAiAnalyzer resumeAiAnalyzer;

    @Override
    public Resume create(CreateResumeRequest request) {
        Resume resume = new Resume();
        resume.setFileName(request.getFileName());
        resume.setStorageKey(request.getStorageKey());
        resume.setResumeText(request.getResumeText());

        return resumeRepository.save(resume);
    }

    @Override
    public Resume analyze(Long id) {
        Resume resume = getById(id);
        if(resume.getResumeText()==null||resume.getResumeText().trim().isEmpty()){
            throw new BusinessException("简历内容为空，无法分析，id="+id);
        }

        //建立正确的状态模型，和后面实现异步化时状态流转保持一致
        resume.setStatus("PROCESSING");
        resumeRepository.save(resume);

        String analysisResult=resumeAiAnalyzer.analyze(resume.getResumeText());
        resume.setAnalysisResult(analysisResult);
        resume.setStatus("COMPLETED");

        return resumeRepository.save(resume);
    }

    @Override
    public Resume getById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(()->new BusinessException("简历不存在，id="+id));
    }

    @Override
    public Resume upload(MultipartFile file) {
        if(file==null||file.isEmpty()){
            throw new RuntimeException("上传文件不能为空");
        }

        String originalFilename=file.getOriginalFilename();
        validateFileType(originalFilename);

        String filePath=localFileStorageService.save(file);

        String resumeText=documentParser.parse(new File(filePath));
        if(resumeText==null||resumeText.trim().isEmpty()){
            throw new BusinessException("简历内容解析为空，无法上传");
        }

        String contentHash= HashUtils.sha256(resumeText);
        resumeRepository.findByContentHash(contentHash).ifPresent(existing->{
            throw new BusinessException("该简历已存在，不能重复上传，resumeId="+existing.getId());
        });

        Resume resume=new Resume();
        resume.setFileName(originalFilename);
        resume.setStorageKey(new File(filePath).getName());
        resume.setFilePath(filePath);
        resume.setResumeText(resumeText);
        resume.setContentHash(contentHash);

        return resumeRepository.save(resume);
    }

    private void validateFileType(String originalFilename) {
        if(originalFilename==null){
            throw new RuntimeException("文件名不能为空");
        }
        String lowerName=originalFilename.toLowerCase();
        if (!(lowerName.endsWith(".pdf")
                || lowerName.endsWith(".doc")
                || lowerName.endsWith(".docx")
                || lowerName.endsWith(".txt"))) {
            throw new RuntimeException("仅支持 pdf、doc、docx、txt 文件上传");
        }
    }

    @Override
    public List<Resume> list() {
        return resumeRepository.findAll();
    }
}
