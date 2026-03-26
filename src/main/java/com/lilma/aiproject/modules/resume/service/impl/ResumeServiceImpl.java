package com.lilma.aiproject.modules.resume.service.impl;

import com.lilma.aiproject.common.constant.ResumeStatusConstants;
import com.lilma.aiproject.common.exception.BusinessException;
import com.lilma.aiproject.common.util.HashUtils;
import com.lilma.aiproject.infrastructure.ai.ResumeAiAnalyzer;
import com.lilma.aiproject.infrastructure.parser.DocumentParser;
import com.lilma.aiproject.infrastructure.parser.TikaDocumentParser;
import com.lilma.aiproject.infrastructure.queue.ResumeAnalyzeTaskProducer;
import com.lilma.aiproject.infrastructure.storage.LocalFileStorageService;
import com.lilma.aiproject.modules.resume.dto.CreateResumeRequest;
import com.lilma.aiproject.modules.resume.entity.Resume;
import com.lilma.aiproject.modules.resume.repository.ResumeRepository;
import com.lilma.aiproject.modules.resume.service.ResumeService;
import com.lilma.aiproject.modules.resume.vo.ResumeListItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private LocalFileStorageService localFileStorageService;
    @Autowired
    private DocumentParser documentParser;
    @Autowired
    private ResumeAnalyzeTaskProducer resumeAnalyzeTaskProducer;

    @Override
    public Resume create(CreateResumeRequest request) {
        Resume resume = new Resume();
        resume.setFileName(request.getFileName());
        resume.setStorageKey(request.getStorageKey());
        resume.setResumeText(request.getResumeText());

        return resumeRepository.save(resume);
    }

    @Override
    public void analyze(Long id) {
        Resume resume = getById(id);
        if(resume.getResumeText()==null||resume.getResumeText().trim().isEmpty()){
            throw new BusinessException("简历内容为空，无法分析，id="+id);
        }

        //防止重复提交
        if(ResumeStatusConstants.PENDING.equals(resume.getStatus())||ResumeStatusConstants.PROCESSING.equals(resume.getStatus())){
            throw new BusinessException("该简历正在分析中，请勿重复提交，id="+id);
        }

        //已提交等待处理
        resume.setStatus(ResumeStatusConstants.PENDING);
        //如果有之前处理失败的重新分析需要把错误原因清空
        resume.setFailReason(null);
        resume.setAnalysisResult(null);
        resumeRepository.save(resume);

        resumeAnalyzeTaskProducer.send(id);
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
    public List<ResumeListItemVO> list() {
        return resumeRepository.findAll()
                .stream()
                .map(this::toListItemVO)
                .collect(Collectors.toList());
    }
    //类型转换
    private ResumeListItemVO toListItemVO(Resume resume){
        ResumeListItemVO vo=new ResumeListItemVO();
        vo.setId(resume.getId());
        vo.setFileName(resume.getFileName());
        vo.setStatus(resume.getStatus());
        vo.setRetryCount(resume.getRetryCount());
        vo.setCreatedAt(resume.getCreatedAt());
        vo.setUpdatedAt(resume.getUpdatedAt());
        vo.setResumeTextPreview(buildPreview(resume.getResumeText(), 100));
        vo.setAnalysisResultPreview(buildPreview(resume.getAnalysisResult(), 120));
        return vo;
    }
    //获取预览数据
    private String buildPreview(String text,int maxLength){
        if(text==null||text.trim().isEmpty())return null;

        //将换行以及多个换行、制表符都清洗为普通空格
        String normalized=text.replaceAll("\\s+"," ").trim();
        if(normalized.length()<=maxLength)return normalized;
        return normalized.substring(0,maxLength)+"...";
    }

    @Override
    public void delete(Long id) {
        Resume resume = getById(id);
        localFileStorageService.delete(resume.getFilePath());
        resumeRepository.delete(resume);
    }
}
