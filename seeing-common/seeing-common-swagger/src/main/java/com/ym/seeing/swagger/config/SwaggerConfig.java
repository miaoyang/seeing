package com.ym.seeing.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/17 20:58
 * @Desc:
 */
@Configuration
@EnableOpenApi
public class SwaggerConfig {

    @Bean
    public Docket seeingApi(){
        return new Docket(DocumentationType.OAS_30)
                .enable(true)
                .apiInfo(getApiInfo())
                .select()
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title("Seeing 接口文档")
                .contact(new Contact("一只想飞的猫🐱","https://github.com/miaoyang/blog","xxxxxx"))
                .version("0.1")
                .license("Apache")
                .build();
    }
}
