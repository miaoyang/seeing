package com.ym.seeing.rpc.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/30 11:44
 * @Desc: 定义输出日志级别
 */
public class OpenFeignLogConfig {
    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.BASIC;
    }
}
