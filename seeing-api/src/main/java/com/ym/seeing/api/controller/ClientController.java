package com.ym.seeing.api.controller;

import com.ym.seeing.core.domain.Msg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 16:18
 * @Desc:
 */
@Controller
@RequestMapping("/api")
@Slf4j
public class ClientController {

    @PostMapping(value = "/uploadbymail")
    @ResponseBody
    public Msg uploadByEmail(
            HttpServletRequest request,
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "mail", defaultValue = "") String mail,
            @RequestParam(value = "pass", defaultValue = "") String pass,
            @RequestParam(value = "days", defaultValue = "0") String days) {
        Msg msg = new Msg();
        if (file == null
                || StringUtils.isBlank(mail)
                || StringUtils.isBlank(pass)) {
            msg.setCode("400");
            msg.setInfo("相关参数不能为空");
            return msg;
        }
        return msg;
    }
}
