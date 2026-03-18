package com.lilma.aiproject.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.upload")
public class FileUploadProperties {
    private String path;

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path=path;
    }
}
