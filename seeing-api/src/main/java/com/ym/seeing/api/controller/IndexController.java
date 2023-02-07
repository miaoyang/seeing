package com.ym.seeing.api.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ym.seeing.api.config.CaptchaProperties;
import com.ym.seeing.api.constant.RedisConstant;
import com.ym.seeing.api.constant.ResultConstant;
import com.ym.seeing.api.constant.UserConstant;
import com.ym.seeing.api.domain.*;
import com.ym.seeing.api.domain.vo.Result;
import com.ym.seeing.api.domain.vo.VerifyCode;
import com.ym.seeing.api.service.*;
import com.ym.seeing.api.util.UserUtil;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.utils.CodeUtil;
import com.ym.seeing.core.utils.IPUtil;
import com.ym.seeing.core.utils.JWTUtil;
import com.ym.seeing.core.utils.VersionUtil;
import com.ym.seeing.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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

    @Autowired
    private IUploadService uploadService;

    @Autowired
    private ImgService imgService;


    @RequestMapping(value = "/")
    public String welcome(Model model, HttpServletRequest httpServletRequest) {
        model.addAttribute("name", "服务端程序");
        model.addAttribute("version", "20221027");
        model.addAttribute("ip", IPUtil.getIpAddr(httpServletRequest));
        model.addAttribute("links", "tbed.hellohao.cn");
        return "welcome";
    }

    @RequestMapping(value = "/webInfo")
    @ResponseBody
    public Msg webInfo() {
        Msg msg = new Msg();
        Config config = configService.getSourceType();
        UploadConfig updateConfig = uploadConfigService.getUpdateConfig();
        SysConfig sysConfig = sysConfigService.getState();
        JSONObject jsonObject = new JSONObject();
        AppClient appClient = appClientService.getAppClientData("app");
        jsonObject.put("webname", config.getWebName());
        jsonObject.put("websubtitle", config.getWebSubTitle());
        jsonObject.put("keywords", config.getWebKeyWords());
        jsonObject.put("description", config.getWebMs());
        jsonObject.put("explain", config.getCExplain());
        jsonObject.put("favicon", config.getWebFavicons());
        jsonObject.put("baidu", config.getBaidu());
        jsonObject.put("links", config.getLinks());
        jsonObject.put("aboutinfo", config.getAboutInfo());
        jsonObject.put("logo", config.getLogo());
        jsonObject.put("api", updateConfig.getApi());
        jsonObject.put("register", sysConfig.getRegister());
        jsonObject.put("isuse", appClient.getIsUse());
        jsonObject.put("clientname", appClient.getAppName());
        jsonObject.put("clientlogo", appClient.getAppLogo());
        jsonObject.put("appupdate", appClient.getAppUpdate());
        msg.setData(jsonObject);
        return msg;
    }

    @PostMapping(value = {"/upload", "/client/upload"})
    public Msg uploadImg(HttpServletRequest request,
                         @RequestParam(value = "file", required = false) MultipartFile multipartFile,
                         Integer day) {
        return uploadService.uploadForLoc(request, multipartFile, day, null);
    }

    @PostMapping(value = {"/uploadForUrl", "/client/uploadForUrl"})
    @ResponseBody
    public Msg uploadForUrl(
            HttpServletRequest request, @RequestParam(value = "data", defaultValue = "") String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        Integer setday = jsonObj.getInteger("day");
        String imgUrl = jsonObj.getString("imgUrl");
        String[] URLArr = imgUrl.split("\n");

        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        UploadConfig updateConfig = uploadConfigService.getUpdateConfig();
        int syscounts = 0;
        if (null == user) {
            syscounts = updateConfig.getImgCountTourists();
        } else {
            syscounts = updateConfig.getImgCountUser();
        }
        Msg retMsg = new Msg();
        JSONObject jsonObject = new JSONObject();
        JSONArray retArray = new JSONArray();
        int errcounts = 0;
        int excess = 0;
        for (int i = 0; i < URLArr.length; i++) {
            int temp = i + 1;
            if (syscounts >= temp) {
                Msg msg = uploadService.uploadForLoc(request, null, setday, URLArr[i]);
                if (!msg.getCode().equals("200")) {
                    errcounts++;
                } else {
                    retArray.add(msg);
                }
            } else {
                excess++;
            }
        }
        jsonObject.put("counts", URLArr.length);
        jsonObject.put("errcounts", errcounts);
        jsonObject.put("excess", excess);
        jsonObject.put("urls", retArray);
        retMsg.setData(jsonObject);
        return retMsg;
    }

    @RequestMapping(value = "/getUploadInfo")
    @ResponseBody
    public Msg getUploadInfo() {
        Msg msg = new Msg();
        JSONObject jsonObject = new JSONObject();
        User user = UserUtil.getUser();
        try {
            UploadConfig updateConfig = uploadConfigService.getUpdateConfig();
            log.debug("uploadConfig: {}",updateConfig);
            if (updateConfig != null) {
                jsonObject.put("suffix", updateConfig.getSuffix().split(","));
                if (null == user) {
                    jsonObject.put("filesize", Integer.valueOf(updateConfig.getFileSizeTourists()) / 1024);
                    jsonObject.put("imgcount", updateConfig.getImgCountTourists());
                    jsonObject.put("uploadSwitch", updateConfig.getIsUpdate());
                    jsonObject.put("uploadInfo", "您登陆后才能使用此功能哦");
                } else {
                    jsonObject.put("filesize", Integer.valueOf(updateConfig.getFileSizeUser()) / 1024);
                    jsonObject.put("imgcount", updateConfig.getImgCountUser());
                    jsonObject.put("uploadSwitch", updateConfig.getUserClose());
                    jsonObject.put("uploadInfo", "系统暂时关闭了用户上传功能");
                }
            }else {
                msg.setInfo("默认的上传配置为空！");
                log.error("error，默认的上传配置为空！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        msg.setData(jsonObject);
        return msg;
    }


    @PostMapping("/verifyCodeForRegister")
    @ResponseBody
    @ApiOperation(value = "生成注册验证码")
    public Msg verifyCodeForRegister() {
        Msg msg = new Msg();
        try {
            msg = generateVerifyCode(RedisConstant.VERIFY_CODE_FOR_REGISTER);
            return msg;
        } catch (Exception e) {
            msg.setCode(ResultConstant.ServerInternalError.getCode());
            msg.setInfo(ResultConstant.ServerInternalError.getInfo());
            log.error("verifyCodeForRegister error: {}", e.getMessage());
            return msg;
        }
    }


    @RequestMapping(value = "/verifyCode", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "生成登录验证码")
    public Msg verifyCode() {
        Msg msg = new Msg();
        try {
            msg = generateVerifyCode(RedisConstant.VERIFY_CODE);
            return msg;
        } catch (Exception e) {
            msg.setCode(ResultConstant.ServerInternalError.getCode());
            msg.setInfo(ResultConstant.ServerInternalError.getInfo());
            log.error("verifyCode error: {}", e.getMessage());
            return msg;
        }
    }

    @PostMapping("/verifyCodeForRetrieve")
    @ResponseBody
    public Msg verifyCodeForRetrieve() {
        Msg msg = new Msg();
        try {
            msg = generateVerifyCode(RedisConstant.VERIFY_CODE_FOR_RETRIEVE);
            return msg;
        } catch (Exception e) {
            msg.setCode(ResultConstant.ServerInternalError.getCode());
            msg.setInfo(ResultConstant.ServerInternalError.getInfo());
            log.error("verifyCodeForRetrieve error: {}", e.getMessage());
            return msg;
        }
    }

    public Msg generateVerifyCode(String redisKey) {
        Msg msg = new Msg();
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(),
                captchaProperties.getCodeCount(), captchaProperties.getThickness());
        captcha.setGenerator(new MathGenerator(1));
        String code = CodeUtil.getVerifyCodeOperator(captcha.getCode());
        String uid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        redisService.setCacheObject(redisKey + uid, code);
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCodeKey(uid);
        verifyCode.setCodeImg(captcha.getImageBase64());
        msg.setData(verifyCode);
        return msg;
    }

    @PostMapping({"/deleImagesByUid", "/client/deleImagesByUid"})
    @ResponseBody
    public Msg deleImagesByUid(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        JSONObject jsonObj = JSONObject.parseObject(data);
        String imguid = jsonObj.getString("imguid");
        Images image = imgService.selectByImgUid(imguid);
        if (null == image) {
            msg.setInfo("图像已不存在，删除成功");
            return msg;
        }
        User user = UserUtil.getUser();
        if (null != user) {
            if (!user.getId().equals(image.getUserId())) {
                msg.setInfo("删除失败，该图片不允许你执行操作");
                msg.setCode("100403");
                return msg;
            }
        }
        List<Integer> idList = imgService.deleteImg(null, image.getId());
        msg.setData(idList);
        List<Long> Delids = (List<Long>) msg.getData();
        if (!Delids.contains(image.getId())) {
            msg.setCode("500");
            msg.setInfo("图像删除失败");
        } else {
            msg.setCode("200");
            msg.setInfo("图像已成功删除");
        }
        return msg;
    }

    @PostMapping("/checkStatus")
    @ResponseBody
    public Msg checkStatus(HttpServletRequest request) {
        Msg msg = new Msg();
        String token = request.getHeader(UserConstant.AUTHORIZATION);
        if (token != null) {
            JSONObject jsonObject = JWTUtil.checkToken(token);
            Boolean isCheck = jsonObject.getBoolean(UserConstant.CHECK);
            if (isCheck) {
                Subject subject = SecurityUtils.getSubject();
                UsernamePasswordToken tokenOBJ = new UsernamePasswordToken(jsonObject.getString("email"),
                        jsonObject.getString("password"));
                tokenOBJ.setRememberMe(true);
                try {
                    subject.login(tokenOBJ);
                    SecurityUtils.getSubject().getSession().setTimeout(3600000);
                    User u = (User) subject.getPrincipal();
                    final JSONObject tempJsonObject = new JSONObject();
                    tempJsonObject.put("RoleLevel", u.getLevel() == 2 ? "admin" : "user");
                    tempJsonObject.put("userName", u.getUserName());
                    msg.setCode(ResultConstant.Code_200);
                    msg.setData(tempJsonObject);
                } catch (Exception e) {
                    msg.setCode(ResultConstant.LoginInvalid.getCode());
                    msg.setInfo(ResultConstant.LoginInvalid.getInfo());
                }
            } else {
                msg.setCode(ResultConstant.LoginInvalid.getCode());
                msg.setInfo(ResultConstant.LoginInvalid.getInfo());
            }
        } else {
            msg.setCode(ResultConstant.LoginNotNow.getCode());
            msg.setInfo(ResultConstant.LoginNotNow.getInfo());
        }
        return msg;
    }

    @PostMapping("/getClientVersion")
    @ResponseBody
    public Msg getClientVersion(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        msg.setCode("000");
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            String appv = jsonObj.getString("version");
            AppClient app = appClientService.getAppClientData("app");
            if (!StringUtils.isBlank(app.getAppUpdate())) {
                String version = app.getAppUpdate().replaceAll("\\.", "");
                if (VersionUtil.compareVersion(app.getAppUpdate(), appv) == 1) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("winpackurl", app.getWinPackUrl());
                    jsonObject.put("macpackurl", app.getMacPackUrl());
                    msg.setCode("200");
                    msg.setData(jsonObject);
                }
            }
        } catch (Exception e) {
            msg.setCode("000");
        }
        return msg;
    }

    @RequestMapping("/jurisError")
    public String jurisError(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/error");
        return "error";
    }

    @RequestMapping("/authError")
    @ResponseBody
    public Msg authError(HttpServletRequest request) {
        Msg msg = new Msg();
        msg.setCode(ResultConstant.AuthenticationFail.getCode());
        msg.setInfo(ResultConstant.AuthenticationFail.getInfo());
        return msg;
    }
}
