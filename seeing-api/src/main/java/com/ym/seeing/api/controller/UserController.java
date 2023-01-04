package com.ym.seeing.api.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ym.seeing.api.annotation.LogAnnotation;
import com.ym.seeing.api.constant.RedisConstant;
import com.ym.seeing.api.constant.ResultConstant;
import com.ym.seeing.api.constant.UserConstant;
import com.ym.seeing.api.domain.*;
import com.ym.seeing.api.domain.vo.Result;
import com.ym.seeing.api.service.*;
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
    public Result register(HttpServletRequest httpServletRequest,
                           @RequestParam(value = "data", defaultValue = "") String data) {
        if (StringUtils.isBlank(data)) {
            log.error("register data is empty!");
            return Result.fail(ResultConstant.InfoEmpty.getCode(), ResultConstant.InfoEmpty.getMsg());
        }
        log.info("register data: {}",data);
        Result result = new Result();
        JSONObject jsonObject = JSONObject.parseObject(data);

        String username = jsonObject.getString(UserConstant.USER_NAME);
        String email = jsonObject.getString(UserConstant.EMAIL);
        String password = Base64Util.encryptBASE64(jsonObject.getString(UserConstant.PASSWORD).getBytes());
        String verifyCodeForRegister = jsonObject.getString(UserConstant.VERIFY_CODE);

        Object cacheVerifyCode = redisService.getCacheObject(RedisConstant.VERIFY_CODE_FOR_REGISTER + httpServletRequest.getHeader(RedisConstant.VERIFY_CODE_FOR_REGISTER_HEADER));

        if (!VerifyUtil.checkEmail(email)) {
            result.setCode(ResultConstant.EmailFormatError.getCode());
            result.setMsg(ResultConstant.EmailFormatError.getMsg());
            return result;
        }
        String regex = "^\\w+$";
        if (username.length() > 20 || !username.matches(regex)) {
            result.setCode(ResultConstant.UserNameTextExceed.getCode());
            result.setMsg(ResultConstant.UserNameTextExceed.getMsg());
            return result;
        }
        if (cacheVerifyCode == null) {
            result.setCode(ResultConstant.VerifyCodeExpired.getCode());
            result.setMsg(ResultConstant.VerifyCodeExpired.getMsg());
            return result;
        }
        if (StringUtils.isBlank(verifyCodeForRegister)) {
            result.setCode(ResultConstant.VerifyCodeEmpty.getCode());
            result.setMsg(ResultConstant.VerifyCodeEmpty.getMsg());
            return result;
        }
        if (cacheVerifyCode.toString().toLowerCase().compareTo(verifyCodeForRegister.toLowerCase()) == 0) {
            User user = new User();
            UploadConfig updateConfig = uploadConfigService.getUpdateConfig();
            EmailConfig emailConfig = emailConfigService.getEmail();
            Integer countUsername = userService.countUsername(username);
            Integer countMail = userService.countMail(email);
            SysConfig sysConfig = sysConfigService.getState();
            if (sysConfig.getRegister() != 1) {
                result.setCode(ResultConstant.CloseUserRegister.getCode());
                result.setMsg(ResultConstant.CloseUserRegister.getMsg());
                return result;
            }
            if (countUsername == 1) {
                result.setCode(ResultConstant.AccountExist.getCode());
                result.setMsg(ResultConstant.AccountExist.getMsg());
                return result;
            }
            if (countMail == 1) {
                result.setCode(ResultConstant.EmailHasRegisted.getCode());
                result.setMsg(ResultConstant.EmailHasRegisted.getMsg());
                return result;
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

                result.setMsg("注册成功,请注意查收邮箱尽快激活账户");
            } else {
                user.setIsOk(1);
                result.setMsg("注册成功,快去登陆吧");
            }
            userService.register(user);
        } else {
            result.setCode(ResultConstant.VerifyCodeError.getCode());
            result.setMsg(ResultConstant.VerifyCodeError.getMsg());
        }

        return result;
    }

    @PostMapping("/login")
    @ResponseBody
    @ApiOperation(value = "用户登录")
    @LogAnnotation(message = "用户登录")
    public Result login(HttpServletRequest httpServletRequest,
                        @RequestParam(value = "data", defaultValue = "") String data) {
        if (StringUtils.isBlank(data)) {
            log.error("login data is empty!");
            return Result.fail(ResultConstant.InfoEmpty.getCode(), ResultConstant.InfoEmpty.getMsg());
        }
        Result result = new Result();
        JSONObject jsonObject = JSONObject.parseObject(data);
        String username = jsonObject.getString(UserConstant.USER_NAME);
        String email = jsonObject.getString(UserConstant.EMAIL);
        String password = Base64Util.encryptBASE64(jsonObject.getString(UserConstant.PASSWORD).getBytes());
        String verifyCode = jsonObject.getString(UserConstant.VERIFY_CODE);
        Object cacheVerifyCode = redisService.getCacheObject(RedisConstant.VERIFY_CODE + httpServletRequest.getHeader(RedisConstant.VERIFY_CODE_HEADER));
        if (cacheVerifyCode == null){
            result.setCode(ResultConstant.VerifyCodeExpired.getCode());
            result.setMsg(ResultConstant.VerifyCodeExpired.getMsg());
            return result;
        }
        if (StringUtils.isBlank(verifyCode)){
            result.setCode(ResultConstant.VerifyCodeEmpty.getCode());
            result.setMsg(ResultConstant.VerifyCodeEmpty.getMsg());
            return result;
        }
        if (cacheVerifyCode.toString().toLowerCase().compareTo(verifyCode.toLowerCase()) == 0){
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken tokenOBJ = new UsernamePasswordToken(email,password);
            tokenOBJ.setRememberMe(true);

            try {
                subject.login(tokenOBJ);
                SecurityUtils.getSubject().getSession().setTimeout(3600000);
                JSONObject json = new JSONObject();
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                // TODO 账号激活问题
                user.setIsOk(1);
                if (user.getIsOk() == 0) {
                    result.setCode(ResultConstant.AccountNoActivated.getCode());
                    result.setMsg(ResultConstant.AccountNoActivated.getMsg());
                    return result;
                }
                if (user.getIsOk() < 0) {
                    result.setCode(ResultConstant.AccountFrozen.getCode());
                    result.setMsg(ResultConstant.AccountFrozen.getMsg());
                    return result;
                }
                String token = JWTUtil.createToken(user);
//                Subject su = SecurityUtils.getSubject();
                json.put("token", token);
                json.put("RoleLevel", user.getLevel() == 2 ? "admin" : "user");
                json.put("userName", user.getUserName());
                result.setData(json);
                return result;
            }catch (Exception e){
                result.setCode(ResultConstant.LoginError.getCode());
                result.setMsg(ResultConstant.LoginError.getMsg());
                log.error("login error ",e.getMessage());
                return result;
            }

        }else {
            result.setCode(ResultConstant.VerifyCodeError.getCode());
            result.setMsg(ResultConstant.VerifyCodeError.getMsg());
        }
        return result;
    }


    @PostMapping("/getUser")
    public User getUser(@RequestBody User user){
       return userService.getUsers(user);
    }


    @GetMapping("/testapp")
    public String testApp() {
        return "seeing 一个为图而生的狂热分子，在这里能看见世界一切美好";
    }
}
