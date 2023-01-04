package com.ym.seeing.api.aop;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Author: Yangmiao
 * @Date: 2022/12/25 21:14
 * @Desc: 日志切面
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
    /**
     * 定义切点
     */
    @Pointcut("execution(public * com.ym.seeing.api.annotation.*.*(..))")
    public void pt(){

    }

    @Around("pt()")
    public Object doLog(ProceedingJoinPoint joinPoint){
        try {
            log.debug("---------------log start----------------");
            Signature signature = joinPoint.getSignature();
            String methodName = signature.getName();
            String interfaceName = signature.getDeclaringTypeName();
            log.debug("method name: {}",methodName);
            log.debug("interface name: {}",interfaceName);
            Object[] args = joinPoint.getArgs();
            log.debug("args: {}", JSON.toJSONString(args));

            long startTime = System.currentTimeMillis();
            Object proceed = joinPoint.proceed();
            log.debug("method run time: {}",startTime-System.currentTimeMillis());
            log.debug("---------------log end------------------");
            return proceed;
        }catch (Exception e){
            log.error("日志切面error: {}",e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
