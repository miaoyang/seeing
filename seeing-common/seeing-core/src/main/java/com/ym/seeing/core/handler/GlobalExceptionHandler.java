package com.ym.seeing.core.handler;

import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.Result;
import com.ym.seeing.core.exception.GlobalException;
import com.ym.seeing.core.exception.StorageSourceInitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yangmiao
 * @Date: 2022/12/19 17:42
 * @Desc: 统一处理异常
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    @ResponseBody
    public ModelAndView handlerGlobalException(GlobalException e, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("error:{}, path:{}",e.getMessage(),requestURI);
        ModelAndView modelAndView = new ModelAndView("/index");
        modelAndView.addObject("error",e.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(StorageSourceInitException.class)
    @ResponseBody
    public ModelAndView handlerStorageException(StorageSourceInitException e,HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("error:{}, path:{}",e.getMessage(),requestURI);
        ModelAndView modelAndView = new ModelAndView("/index");
        modelAndView.addObject("error",e.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ModelAndView handlerStorageException(Exception e, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("error:{}, path:{}",e.getMessage(),requestURI);
        ModelAndView modelAndView = new ModelAndView("/index");
        modelAndView.addObject("error",e.getMessage());
        return modelAndView;
    }


}
