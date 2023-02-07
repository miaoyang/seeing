package com.ym.seeing.api.controller;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.HexUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ym.seeing.api.annotation.LogAnnotation;
import com.ym.seeing.api.constant.RedisConstant;
import com.ym.seeing.api.constant.ResultConstant;
import com.ym.seeing.api.constant.UserConstant;
import com.ym.seeing.api.domain.*;
import com.ym.seeing.api.domain.vo.Result;
import com.ym.seeing.api.service.*;
import com.ym.seeing.api.shiro.SubjectFilter;
import com.ym.seeing.api.util.SendEmailUtil;
import com.ym.seeing.api.util.UserUtil;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.utils.Base64Util;
import com.ym.seeing.core.utils.DateUtil;
import com.ym.seeing.core.utils.JWTUtil;
import com.ym.seeing.core.utils.VerifyUtil;
import com.ym.seeing.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 10:03
 * @Desc:
 */
@Controller
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IEmailConfigService emailConfigService;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private IUploadConfigService uploadConfigService;
    @Autowired
    private IConfigService configService;
    @Autowired
    private RedisService redisService;

    @PostMapping("/register")
    @ResponseBody
    @ApiOperation(value = "用户注册")
    @LogAnnotation(message = "用户注册")
    public Msg register(HttpServletRequest httpServletRequest,
                        @RequestParam(value = "data", defaultValue = "") String data) {
        if (StringUtils.isBlank(data)) {
            log.error("register data is empty!");
            return Msg.fail(ResultConstant.InfoEmpty.getCode(), ResultConstant.InfoEmpty.getInfo());
        }
        log.info("register data: {}", data);
        Msg msg = new Msg();
        JSONObject jsonObject = JSONObject.parseObject(data);

        String username = jsonObject.getString(UserConstant.USER_NAME);
        String email = jsonObject.getString(UserConstant.EMAIL);
        String password = Base64Util.encryptBASE64(jsonObject.getString(UserConstant.PASSWORD).getBytes());
        String verifyCodeForRegister = jsonObject.getString(UserConstant.VERIFY_CODE);

        Object cacheVerifyCode = redisService.getCacheObject(RedisConstant.VERIFY_CODE_FOR_REGISTER + httpServletRequest.getHeader(RedisConstant.VERIFY_CODE_FOR_REGISTER_HEADER));

        if (!VerifyUtil.checkEmail(email)) {
            msg.setCode(ResultConstant.EmailFormatError.getCode());
            msg.setInfo(ResultConstant.EmailFormatError.getInfo());
            return msg;
        }
        String regex = "^\\w+$";
        if (username.length() > 20 || !username.matches(regex)) {
            msg.setCode(ResultConstant.UserNameTextExceed.getCode());
            msg.setInfo(ResultConstant.UserNameTextExceed.getInfo());
            return msg;
        }
        if (cacheVerifyCode == null) {
            msg.setCode(ResultConstant.VerifyCodeExpired.getCode());
            msg.setInfo(ResultConstant.VerifyCodeExpired.getInfo());
            return msg;
        }
        if (StringUtils.isBlank(verifyCodeForRegister)) {
            msg.setCode(ResultConstant.VerifyCodeEmpty.getCode());
            msg.setInfo(ResultConstant.VerifyCodeEmpty.getInfo());
            return msg;
        }
        if (cacheVerifyCode.toString().toLowerCase().compareTo(verifyCodeForRegister.toLowerCase()) == 0) {
            User user = new User();
            UploadConfig updateConfig = uploadConfigService.getUpdateConfig();
            EmailConfig emailConfig = emailConfigService.getEmail();
            Integer countUsername = userService.countUsername(username);
            Integer countMail = userService.countMail(email);
            SysConfig sysConfig = sysConfigService.getState();
            if (sysConfig.getRegister() != 1) {
                msg.setCode(ResultConstant.CloseUserRegister.getCode());
                msg.setInfo(ResultConstant.CloseUserRegister.getInfo());
                return msg;
            }
            if (countUsername == 1) {
                msg.setCode(ResultConstant.AccountExist.getCode());
                msg.setInfo(ResultConstant.AccountExist.getInfo());
                return msg;
            }
            if (countMail == 1) {
                msg.setCode(ResultConstant.EmailHasRegisted.getCode());
                msg.setInfo(ResultConstant.EmailHasRegisted.getInfo());
                return msg;
            }
            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            user.setBirthder(DateUtil.dateToStr(new Date()));
            user.setLevel(1);
            user.setUid(uuid);
            user.setMemory(updateConfig.getUserMemory());
            user.setGroupId(1);
            user.setEmail(email);
            user.setUserName(username);
            user.setPassword(password);

            Config config = configService.getSourceType();
            if (emailConfig.getIsUsing() == 1) {
                user.setIsOk(0);
                // 发送邮件

                msg.setInfo("注册成功,请注意查收邮箱尽快激活账户");
            } else {
                user.setIsOk(1);
                msg.setInfo("注册成功,快去登陆吧");
            }
            userService.register(user);
        } else {
            msg.setCode(ResultConstant.VerifyCodeError.getCode());
            msg.setInfo(ResultConstant.VerifyCodeError.getInfo());
        }

        return msg;
    }

    @PostMapping("/login")
    @ResponseBody
    @ApiOperation(value = "用户登录")
    @LogAnnotation(message = "用户登录")
    public Msg login(HttpServletRequest httpServletRequest,
                     @RequestParam(value = "data", defaultValue = "") String data) {
        if (StringUtils.isBlank(data)) {
            log.error("login data is empty!");
            return Msg.fail(String.valueOf(ResultConstant.InfoEmpty.getCode()), "", ResultConstant.InfoEmpty.getInfo());
        }
        Msg msg = new Msg();
        JSONObject jsonObject = JSONObject.parseObject(data);
        String username = jsonObject.getString(UserConstant.USER_NAME);
        String email = jsonObject.getString(UserConstant.EMAIL);
        String password = Base64Util.encryptBASE64(jsonObject.getString(UserConstant.PASSWORD).getBytes());
        String verifyCode = jsonObject.getString(UserConstant.VERIFY_CODE);
        Object cacheVerifyCode = redisService.getCacheObject(RedisConstant.VERIFY_CODE + httpServletRequest.getHeader(RedisConstant.VERIFY_CODE_HEADER));

        if (cacheVerifyCode == null) {
            msg.setCode(String.valueOf(ResultConstant.VerifyCodeExpired.getCode()));
            msg.setInfo(ResultConstant.VerifyCodeExpired.getInfo());
            return msg;
        }
        if (StringUtils.isBlank(verifyCode)) {
            msg.setCode(String.valueOf(ResultConstant.VerifyCodeEmpty.getCode()));
            msg.setInfo(ResultConstant.VerifyCodeEmpty.getInfo());
            return msg;
        }
        if (cacheVerifyCode.toString().toLowerCase().compareTo(verifyCode.toLowerCase()) == 0) {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken tokenOBJ = new UsernamePasswordToken(email, password);
            tokenOBJ.setRememberMe(true);

            try {
                subject.login(tokenOBJ);
                SecurityUtils.getSubject().getSession().setTimeout(3600000);
                JSONObject json = new JSONObject();
                User user = UserUtil.getUser();
                // TODO 账号激活问题
                user.setIsOk(1);
                if (user.getIsOk() == 0) {
                    msg.setCode(String.valueOf(ResultConstant.AccountNoActivated.getCode()));
                    msg.setInfo(ResultConstant.AccountNoActivated.getInfo());
                    return msg;
                }
                if (user.getIsOk() < 0) {
                    msg.setCode(String.valueOf(ResultConstant.AccountFrozen.getCode()));
                    msg.setInfo(ResultConstant.AccountFrozen.getInfo());
                    return msg;
                }
                String token = JWTUtil.createToken(user);
                json.put("token", token);
                json.put("RoleLevel", user.getLevel() == 2 ? "admin" : "user");
                json.put("userName", user.getUserName());
                msg.setData(json);
                log.info("login: {}", msg);
                return msg;
            } catch (Exception e) {
                msg.setCode(String.valueOf(ResultConstant.LoginError.getCode()));
                msg.setInfo(ResultConstant.LoginError.getInfo());
                log.error("login error ", e.getMessage());
                return msg;
            }

        } else {
            msg.setCode(String.valueOf(ResultConstant.VerifyCodeError.getCode()));
            msg.setInfo(ResultConstant.VerifyCodeError.getInfo());
        }
        return msg;
    }

    @RequestMapping(value = "/activation", method = RequestMethod.GET)
    @ApiOperation(value = "激活账号")
    public String activation(Model model, String activation, String username) {
        User user = new User();
        user.setUid(activation);
        User users = userService.getUsers(user);
        model.addAttribute("webhost", SubjectFilter.WEBHOST);
        if (users != null && user.getIsOk() == 0) {
            userService.updateUserUid(activation);
            model.addAttribute("title", "激活成功");
            model.addAttribute("name", "Hi~" + username);
            model.addAttribute("note", "您的账号已成功激活看");
            return "msg";
        } else {
            model.addAttribute("title", "操作无效");
            model.addAttribute("name", "该页面为无效页面");
            model.addAttribute("note", "请返回首页");
            return "msg";
        }
    }

    @PostMapping("/logout")
    @ResponseBody
    @ApiOperation(value = "退出账号")
    public Msg logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        log.debug("退出账号，操作成功！");
        return Msg.ok("操作成功");
    }

    @PostMapping("/retrievePass")
    @ResponseBody
    @ApiOperation(value = "重设密码")
    public Msg retrievePass(HttpServletRequest httpServletRequest,
                            @RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            String email = jsonObj.getString("email");
            String retrieveCode = jsonObj.getString("retrieveCode");
            Object verifyCodeRedis = redisService.getCacheObject("verifyCodeForRetrieve_" +
                    httpServletRequest.getHeader("verifyCodeForRetrieve"));
            EmailConfig emailConfig = emailConfigService.getEmail();
            if (null == verifyCodeRedis) {
                msg.setCode("4035");
                msg.setInfo("验证码已失效，请重新弄获取。");
                return msg;
            } else if (null == retrieveCode) {
                msg.setCode("4036");
                msg.setInfo("验证码不能为空。");
                return msg;
            }
            if ((verifyCodeRedis.toString().toLowerCase()).compareTo((retrieveCode.toLowerCase())) != 0) {
                msg.setCode("40034");
                msg.setInfo("验证码不正确");
                return msg;
            }
            Integer ret = userService.countMail(email);
            if (ret > 0) {
                if (emailConfig.getIsUsing() == 1) {
                    User u2 = new User();
                    u2.setEmail(email);
                    User user = userService.getUsers(u2);
                    if (user.getIsOk() == -1) {
                        msg.setCode("110110");
                        msg.setInfo("当前用户已被冻结，禁止操作");
                        return msg;
                    }
                    Config config = configService.getSourceType();
                    SendEmailUtil.sendEmailFindPass(emailConfig, user.getUserName(), user.getUid(), user.getEmail(), config);
                    msg.setInfo("重置密码的验证链接已发送至该邮箱，请前往邮箱验证并重置密码。【若长时间未收到邮件，请检查垃圾箱】");
                } else {
                    msg.setCode("400");
                    msg.setInfo("本站暂未开启邮箱服务，请联系管理员");
                }
            } else {
                msg.setCode("110404");
                msg.setInfo("未找到邮箱所在的用户");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("系统发生错误");
        }
        return msg;
    }

    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public String retrieve(Model model, String activation, String cip) {
        Integer ret = 0;
        try {
            User u2 = new User();
            u2.setUid(activation);
            User user = userService.getUsers(u2);
            user.setIsOk(1);
            //解密密码
            String newPass = HexUtil.decodeHexStr(cip);
            user.setPassword(Base64Util.encryptBASE64(newPass.getBytes()));
            String uid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            user.setUid(uid);
            Integer r = userService.changeUser(user);
            model.addAttribute("title", "成功");
            model.addAttribute("name", "新密码:" + newPass);
            model.addAttribute("note", "密码已被系统重置，请即使登录修改你的新密码");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("title", "抱歉");
            model.addAttribute("name", "系统操作过程中发生错误");
            model.addAttribute("note", "操作失败");
        }
        model.addAttribute("webhost", SubjectFilter.WEBHOST);
        return "msg";
    }


    @PostMapping("/getUser")
    public User getUser(@RequestBody User user) {
        return userService.getUsers(user);
    }

}
