package com.ym.seeing.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/23 16:43
 * @Desc:
 */
@Controller
public class AdminController {

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        return "yangmaio";
    }
}
