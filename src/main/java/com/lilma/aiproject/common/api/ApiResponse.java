package com.lilma.aiproject.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T>{
    private int code;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(0,data,"success");
    }

    public static ApiResponse<?> success(){
        return new ApiResponse<>(0,null,"success");
    }

    public static ApiResponse<?> fail(int code,String message){
        return new ApiResponse<>(code,null,message);
    }
}
