package com.lilma.aiproject.common.exception;

import com.lilma.aiproject.common.api.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException ex){
        String message=ex.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResponse.fail(400,message);
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException ex){
        return ApiResponse.fail(400,ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception ex){
        return ApiResponse.fail(500,ex.getMessage());
    }
}