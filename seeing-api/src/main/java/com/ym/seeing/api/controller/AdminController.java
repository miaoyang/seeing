package com.ym.seeing.api.controller;

import com.ym.seeing.api.domain.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/23 16:43
 * @Desc:
 */
@Controller
public class AdminController {

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    @ResponseBody
    public String test(){
        return "yangmaio哈哈哈哈哈哈";
    }

    @RequestMapping(value = "/testpost",method = RequestMethod.POST)
    @ResponseBody
    public Result testPost(){
        return Result.ok("yangmiao");
    }
}
