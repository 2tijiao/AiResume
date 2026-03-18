package com.lilma.aiproject.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtils {

    public static String sha256(String text){
        try{
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] hashBytes=digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb=new StringBuilder();
            for(byte b:hashBytes)sb.append(String.format("%02x",b));
            return sb.toString();
        }catch (Exception e){
            throw new RuntimeException("生成哈希失败："+e.getMessage(),e);
        }
    }
}
