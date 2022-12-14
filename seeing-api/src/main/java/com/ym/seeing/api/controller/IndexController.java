package com.ym.seeing.api.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import com.alibaba.fastjson.JSONObject;
import com.ym.seeing.api.config.CaptchaProperties;
import com.ym.seeing.api.constant.RedisConstant;
import com.ym.seeing.api.constant.ResultConstant;
import com.ym.seeing.api.constant.UserConstant;
import com.ym.seeing.api.domain.vo.Result;
import com.ym.seeing.api.domain.vo.VerifyCode;
import com.ym.seeing.api.service.IAppClientService;
import com.ym.seeing.api.service.IConfigService;
import com.ym.seeing.api.service.ISysConfigService;
import com.ym.seeing.api.service.IUploadConfigService;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.utils.CodeUtil;
import com.ym.seeing.core.utils.IPUtil;
import com.ym.seeing.core.utils.JWTUtil;
import com.ym.seeing.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/24 10:04
 * @Desc: 主页的controller操作
 */
@Controller
@Api(tags = "主页")
@Slf4j
public class IndexController {

    @Autowired
    private IUploadConfigService uploadConfigService;

    @Autowired
    private IConfigService configService;

    @Autowired
    private ISysConfigService sysConfigService;

    @Autowired
    private IAppClientService appClientService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CaptchaProperties captchaProperties;



    @RequestMapping(value = "/")
    public String welcome(Model model, HttpServletRequest httpServletRequest) {
        model.addAttribute("name","服务端程序");
        model.addAttribute("version","20221027");
        model.addAttribute("ip", IPUtil.getIpAddr(httpServletRequest));
        model.addAttribute("links","tbed.hellohao.cn");
        return "welcome";
    }

    @RequestMapping(value = "/webInfo")
    @ResponseBody
    public Result webInfo(){
        Result result = new Result();
        return result;
    }


    @PostMapping("/verifyCodeForRegister")
    @ResponseBody
    @ApiOperation(value = "生成注册验证码")
    public Result verifyCodeForRegister() {
        Result result = new Result();
        try {
            result = generateVerifyCode(RedisConstant.VERIFY_CODE_FOR_REGISTER);
            return result;
        } catch (Exception e) {
            result.setCode(ResultConstant.ServerInternalError.getCode());
            result.setMsg(ResultConstant.ServerInternalError.getMsg());
            log.error("verifyCodeForRegister error: {}",e.getMessage());
            return result;
        }
    }


    @RequestMapping(value = "/verifyCode",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "生成登录验证码")
    public Result verifyCode(){
        Result result = new Result();
        try {
            result = generateVerifyCode(RedisConstant.VERIFY_CODE);
            return result;
        }catch (Exception e){
            result.setCode(ResultConstant.ServerInternalError.getCode());
            result.setMsg(ResultConstant.ServerInternalError.getMsg());
            log.error("verifyCode error: {}",e.getMessage());
            return result;
        }
    }

    @PostMapping("/verifyCodeForRetrieve")
    @ResponseBody
    public Result verifyCodeForRetrieve(){
        Result result = new Result();
        try {
            result = generateVerifyCode(RedisConstant.VERIFY_CODE_FOR_RETRIEVE);
            return result;
        }catch (Exception e){
            result.setCode(ResultConstant.ServerInternalError.getCode());
            result.setMsg(ResultConstant.ServerInternalError.getMsg());
            log.error("verifyCodeForRetrieve error: {}",e.getMessage());
            return result;
        }
    }

    public Result generateVerifyCode(String redisKey){
        Result result = new Result();
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(),
                captchaProperties.getCodeCount(), captchaProperties.getThickness());
        captcha.setGenerator(new MathGenerator(1));
        String code = CodeUtil.getVerifyCodeOperator(captcha.getCode());
        String uid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        redisService.setCacheObject(redisKey + uid, code);
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCodeKey(uid);
        verifyCode.setCodeImg(captcha.getImageBase64());
        result.setData(verifyCode);
        return result;
    }

    @PostMapping("/checkStatus")
    @ResponseBody
    public Result checkStatus(HttpServletRequest request){
        Result result = new Result();
        String token = request.getHeader(UserConstant.AUTHORIZATION);
        if (token != null){
            JSONObject jsonObject = JWTUtil.checkToken(token);
            Boolean isCheck = jsonObject.getBoolean(UserConstant.CHECK);
            if (isCheck){
                Subject subject = SecurityUtils.getSubject();
                UsernamePasswordToken tokenOBJ = new UsernamePasswordToken(jsonObject.getString("email"),
                        jsonObject.getString("password"));
                tokenOBJ.setRememberMe(true);
                try {
                    subject.login(tokenOBJ);
                    SecurityUtils.getSubject().getSession().setTimeout(3600000);
                    User u = (User) subject.getPrincipal();
                    final JSONObject tempJsonObject = new JSONObject();
                    tempJsonObject.put("RoleLevel",u.getLevel()==2?"admin":"user");
                    tempJsonObject.put("userName",u.getUserName());
                    result.setCode(ResultConstant.Code_200);
                    result.setData(tempJsonObject);
                }catch (Exception e){
                    result.setCode(ResultConstant.LoginInvalid.getCode());
                    result.setMsg(ResultConstant.LoginInvalid.getMsg());
                }
            }else {
                result.setCode(ResultConstant.LoginInvalid.getCode());
                result.setMsg(ResultConstant.LoginInvalid.getMsg());
            }
        }else {
            result.setCode(ResultConstant.LoginNotNow.getCode());
            result.setMsg(ResultConstant.LoginNotNow.getMsg());
        }
        return result;
    }

    @RequestMapping("/jurisError")
    @ResponseBody
    public Result jurisError(HttpServletRequest request){
        Result msg = new Result();
        msg.setCode(ResultConstant.AuthenticationFail.getCode());
        msg.setMsg(ResultConstant.AuthenticationFail.getMsg());
        return msg;
    }

    @RequestMapping("/authError")
    @ResponseBody
    public Result authError(HttpServletRequest request){
        Result msg = new Result();
        msg.setCode(ResultConstant.AuthenticationFail.getCode());
        msg.setMsg(ResultConstant.AuthenticationFail.getMsg());
        return msg;
    }
}
