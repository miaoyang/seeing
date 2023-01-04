package com.ym.seeing.actuator;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: Yangmiao
 * @Date: 2022/12/30 18:22
 * @Desc: 后台监控启动类
 */
@SpringBootApplication
@EnableAdminServer
public class ActuatorApp {
    public static void main(String[] args) {
        SpringApplication.run(ActuatorApp.class,args);
    }
}
