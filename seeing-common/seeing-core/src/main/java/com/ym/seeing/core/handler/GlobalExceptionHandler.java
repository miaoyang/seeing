package com.ym.seeing.core.handler;

import com.ym.seeing.core.domain.Result;
import com.ym.seeing.core.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2022/12/19 17:42
 * @Desc: 统一处理异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    @ResponseBody
    public Result handlerGlobalException(GlobalException e, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("error:{}, path:{}",e.getMessage(),requestURI);
        return Result.fail(e.getMessage());
    }

}
