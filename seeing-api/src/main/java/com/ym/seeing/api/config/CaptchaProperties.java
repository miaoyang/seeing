package com.ym.seeing.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/24 10:09
 * @Desc: 验证码属性配置
 */
@Configuration
@ConfigurationProperties(prefix = "captcha")
@Data
public class CaptchaProperties {

    private int width;

    private int height;

    private int codeCount;

    private int thickness;
}
