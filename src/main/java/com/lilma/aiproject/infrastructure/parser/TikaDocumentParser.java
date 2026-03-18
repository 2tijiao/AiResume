package com.lilma.aiproject.infrastructure.parser;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TikaDocumentParser implements DocumentParser{

    private final Tika tika=new Tika();

    @Override
    public String parse(File file) {
        try{
            return tika.parseToString(file);
        }catch (Exception e){
            throw new RuntimeException("文档解析失败："+e.getMessage(),e);
        }
    }
}
