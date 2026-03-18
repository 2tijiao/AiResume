package com.lilma.aiproject.infrastructure.storage;

import com.lilma.aiproject.common.config.FileUploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class LocalFileStorageServiceImpl implements LocalFileStorageService{
    @Autowired
    private FileUploadProperties fileUploadProperties;

    @Override
    public String save(MultipartFile file) {
        String originalFilename=file.getOriginalFilename();
        String extension=getExtension(originalFilename);

        String newFileName= UUID.randomUUID().toString().replace("-","")+extension;

        File uploadDir=new File(fileUploadProperties.getPath());
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }

        File targetFile=new File(uploadDir,newFileName);

        try{
            file.transferTo(targetFile);
        }catch (IOException E){
            throw new RuntimeException("文件保存失败："+E.getMessage());
        }

        return targetFile.getAbsolutePath();
    }

    private String getExtension(String fileName){
        if(fileName==null||!fileName.contains("."))return "";
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
