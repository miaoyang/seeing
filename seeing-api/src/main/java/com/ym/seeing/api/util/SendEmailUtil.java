package com.ym.seeing.api.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import com.ym.seeing.api.domain.Config;
import com.ym.seeing.api.domain.EmailConfig;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/6 11:26
 * @Desc: 发送邮件
 * https://pebbletemplates.io/wiki/guide/spring-boot-integration/
 */
@Slf4j
@Component
public class SendEmailUtil {
    /**
     * 发送邮件
     * @param emailConfig
     * @param username
     * @param uid
     * @param toEmail
     * @param config
     * @return
     */
    @Async("taskExecutor")
    public static Integer sendEmail(EmailConfig emailConfig, String username, String uid, String toEmail, Config config){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.debug", "false");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug",  "false");
        props.put("mail.smtp.timeout", "20000");
        props.put("mail.smtp.port", emailConfig.getPort());
        props.put("mail.smtp.host", emailConfig.getEmailUrl());

        OhMyEmail.config(props,emailConfig.getEmails(),emailConfig.getEmailKey());

        String webName=config.getWebName();
        String domain = config.getDomain();

        try {
            PebbleEngine pebbleEngine = new PebbleEngine.Builder().build();
            ClassPathResource classPathResource = new ClassPathResource("/templates/emailRegister.html");
            PebbleTemplate pebbleTemplate = pebbleEngine.getTemplate(classPathResource.getPath());
            Map<String, Object> context = new HashMap<>();
            context.put("username", username);
            context.put("webname", webName);
            context.put("url", domain+"/user/activation?activation="+uid+"&username=" + username );
            Writer writer = new StringWriter();
            pebbleTemplate.evaluate(writer,context);
            String output = writer.toString();
            OhMyEmail.subject(webName+"账号激活")
                    .from(webName)
                    .to(toEmail)
                    .html(output)
                    .send();
            return 1;
        }catch (IOException e) {
            e.printStackTrace();
            log.error("sendEmail: error,{}",e.getMessage());
            return 0;
        } catch (SendMailException e) {
            e.printStackTrace();
            log.error("sendEmail: error,{}",e.getMessage());
            return 0;
        }
    }

    /**
     * 发送找回密码的邮件
     * @param emailConfig
     * @param username
     * @param uid
     * @param toEmail
     * @param config
     * @return
     */
    @Async("taskExecutor")
    public static Integer sendEmailFindPass(EmailConfig emailConfig,String username, String uid, String toEmail,  Config config) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.debug", "false");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug",  "false");
        props.put("mail.smtp.timeout", "20000");
        props.put("mail.smtp.port", emailConfig.getPort());
        props.put("mail.smtp.host", emailConfig.getEmailUrl());

        OhMyEmail.config(props, emailConfig.getEmails(), emailConfig.getEmailKey());
        String webName=config.getWebName();
        String domain = config.getDomain();
        String newPass = UUID.randomUUID().toString().replace("-", "").toLowerCase().substring(0,10);
        try {
            //生成模板
            PebbleEngine engine = new PebbleEngine.Builder().build();
            ClassPathResource classPathResource = new ClassPathResource("/templates/emailFindPass.html");
            PebbleTemplate compiledTemplate = engine.getTemplate(classPathResource.getPath());
            Map<String, Object> context = new HashMap<>();
            context.put("username", username);
            context.put("webname", webName);
            context.put("new_pass", newPass);
            context.put("url",domain+"/user/retrieve?activation=" + uid+"&cip="+ HexUtil.encodeHexStr(newPass, CharsetUtil.CHARSET_UTF_8));
            Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);
            String output = writer.toString();
            OhMyEmail.subject(webName+"密码重置")
                    .from(webName)
                    .to(toEmail)
                    .html(output)
                    .send();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sendEmailFindPass: error,{}",e.getMessage());
            return 0;
        }
    }
}
