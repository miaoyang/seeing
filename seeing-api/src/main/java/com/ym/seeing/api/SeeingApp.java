package com.ym.seeing.api;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 09:54
 * @Desc:
 */

@ComponentScan(basePackages = {"com.ym.seeing.*"})
@EnableFeignClients
@SpringBootApplication
@EnableAdminServer
public class SeeingApp {

    public static void main(String[] args) {
        SpringApplication.run(SeeingApp.class,args);
    }
}
