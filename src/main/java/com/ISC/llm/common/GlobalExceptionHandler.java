package com.ISC.llm.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e, HttpServletRequest request) {
        log.error("An error occurred while processing request [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(500, "Internal Server Error: " + e.getMessage());
    }

    /**
     * Handle business exceptions here (if you define a CustomBusinessException)
     */
}
