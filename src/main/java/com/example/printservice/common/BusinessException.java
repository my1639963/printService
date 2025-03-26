package com.example.printservice.common;

/**
 * 业务异常类
 * 用于处理业务逻辑相关的异常
 */
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 