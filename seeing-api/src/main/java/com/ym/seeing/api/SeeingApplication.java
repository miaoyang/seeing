package com.ym.seeing.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 09:54
 * @Desc:
 */
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@MapperScan(basePackages = "com.ym.seeing.api.mapper")
@SpringBootApplication
public class SeeingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeeingApplication.class,args);
    }
}
