package com.ym.seeing.api.controller;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.ym.seeing.api.domain.UploadConfig;
import com.ym.seeing.api.service.IUploadConfigService;
import com.ym.seeing.api.service.IUploadService;
import com.ym.seeing.api.service.IUserService;
import com.ym.seeing.api.service.ImgService;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.utils.Base64ToMultipartFileUtil;
import com.ym.seeing.core.utils.JWTUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 16:22
 * @Desc:
 */
@Controller
@Slf4j
@Api("客户端APP")
public class ClientAppController {
    @Autowired
    private ImgService imgService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUploadConfigService uploadConfigService;
    @Autowired
    private IUploadService uploadService;

    @PostMapping("/loginByToken")
    @ResponseBody
    public Msg loginByToken(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            JSONObject jsonObject = new JSONObject();
            User newUser = new User();
            String userToken = jsonObj.getString("userToken");
            newUser.setToken(userToken);
            User userData = userService.loginByToken(userToken);
            if (userData.getIsOk() < 1) {
                SecurityUtils.getSubject().logout();
                msg.setInfo("账号暂时无法使用，请咨询管理员");
                msg.setCode("110403");
                return msg;
            }
            String jwtToken = JWTUtil.createToken(userData);
            UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
            jsonObject.put("username", userData.getUserName());
            jsonObject.put("jwttoken", jwtToken);
            jsonObject.put("suffix", uploadConfig.getSuffix().split(","));
            jsonObject.put("myImgTotal", imgService.countImg(userData.getId()));
            jsonObject.put("filesize", Integer.valueOf(uploadConfig.getFileSizeUser()) / 1024);
            jsonObject.put("imgcount", uploadConfig.getImgCountUser());
            jsonObject.put("uploadSwitch", uploadConfig.getUserClose());
            long memory = Long.valueOf(userData.getMemory());
            Long userMemory = imgService.getUserMemory(userData.getId()) == null ? 0L : imgService.getUserMemory(userData.getId());
            if (memory == 0) {
                jsonObject.put("myMemory", "无容量");
            } else {
                Double aDouble = Double.valueOf(String.format("%.2f", (((double) userMemory / (double) memory) * 100)));
                if (aDouble >= 999) {
                    jsonObject.put("myMemory", 999);
                } else {
                    jsonObject.put("myMemory", aDouble);
                }
            }
            msg.setData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("未获取到用户，请先在设置中添加Token");
        }
        return msg;
    }

    @PostMapping("/imgUpload")
    public Msg imgUpload(HttpServletRequest request, @RequestParam(required = true, value = "file") MultipartFile file,
                         @RequestParam(required = true, value = "days") String days) {
        Msg msg = new Msg();
        try {
            if (null != file) {
                msg = uploadService.uploadForLoc(request, file, Integer.valueOf(days), null);
            } else {
                msg.setCode("500");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("500");
        }
        return msg;
    }

    @PostMapping("/imgUploadForCopy")
    public Msg imgUploadForCopy(@RequestParam(required = true, value = "data") String data, HttpServletRequest request) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            String imgStr = jsonObj.getString("imgstr");
            Integer days = jsonObj.getInteger("days");
            if (null != imgStr && !imgStr.isEmpty()) {
                MultipartFile multipartFile = Base64ToMultipartFileUtil.base64Convert(imgStr);
                msg = uploadService.uploadForLoc(request, multipartFile, days, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("500");
        }
        return msg;
    }
}
