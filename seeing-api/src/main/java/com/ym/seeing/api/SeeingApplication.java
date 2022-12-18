package com.ym.seeing.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 09:54
 * @Desc:
 */

@ComponentScan(basePackages = {"com.ym.seeings"})
@EnableFeignClients
@SpringBootApplication
public class SeeingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeeingApplication.class,args);
    }
}
