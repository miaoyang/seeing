package com.ym.seeing.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ym.seeing.api.domain.*;
import com.ym.seeing.api.service.*;
import com.ym.seeing.api.util.SendEmailUtil;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.utils.FileUtil;
import com.ym.seeing.datasource.upload.GetSource;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/2 14:35
 * @Desc:
 */
@Controller
@Slf4j
@RequestMapping("/admin/root")
@Api(tags = "管理员操作")
public class AdminRootController {
    @Autowired
    private IConfigService configService;
    @Autowired
    private IKeyService keysService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IEmailConfigService emailConfigService;
    @Autowired
    private IUploadConfigService uploadConfigService;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private ImgService imgService;
    @Autowired
    private ImgReviewService imgreviewService;
    @Autowired
    private IAppClientService appClientService;

    @Autowired
    private GetSource getSource;


    @PostMapping(value = "/getUserList")
    @ResponseBody
    public Msg getUserList(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            Integer pageNum = jsonObj.getInteger("pageNum");
            Integer pageSize = jsonObj.getInteger("pageSize");
            String queryText = jsonObj.getString("queryText");
            PageHelper.startPage(pageNum, pageSize);
            List<User> users = userService.getUserList(queryText);
            PageInfo<User> rolePageInfo = new PageInfo<>(users);
            msg.setData(rolePageInfo);
        } catch (Exception e) {
            msg.setCode("500");
        }
        return msg;
    }

    @PostMapping(value = "/updateUserInfo")
    @ResponseBody
    public Msg updateUserInfo(@RequestParam(value = "data", defaultValue = "") String data) {
        final Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            Integer id = jsonObj.getInteger("id");
            String email = jsonObj.getString("email");
            Long memory = jsonObj.getLong("memory");
            Integer groupid = jsonObj.getInteger("groupid");
            Integer isok = jsonObj.getInteger("isok");
            if (memory < 0 || memory > 1048576L) {
                msg.setCode("500");
                msg.setInfo("容量不得超过1048576");
                return msg;
            }
            final User user = new User();
            final User user2 = new User();
            user2.setId(id);
            User userInfo = userService.getUsers(user2);
            user.setId(id);
            user.setEmail(email);
            user.setMemory(Long.toString(memory * 1024 * 1024));
            user.setGroupId(groupid);
            if (userInfo.getLevel() == 1) {
                user.setIsOk(isok == 1 ? 1 : -1);
            }
            userService.changeUser(user);
            msg.setInfo("修改成功");
        } catch (Exception e) {
            msg.setCode("500");
            msg.setInfo("修改失败");
            e.printStackTrace();
        }
        return msg;
    }

    @PostMapping("/disableUser")
    @ResponseBody
    public Msg disableUser(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            JSONArray userIdList = jsonObj.getJSONArray("arr");
            for (int i = 0; i < userIdList.size(); i++) {
                User u = new User();
                u.setId(userIdList.getInteger(i));
                User u2 = userService.getUsers(u);
                if (u2.getLevel() == 1) {
                    User user = new User();
                    user.setId(userIdList.getInteger(i));
                    user.setIsOk(-1);
                    userService.changeUser(user);
                }

            }
            msg.setInfo("所选用户已被禁用");
        } catch (Exception e) {
            e.printStackTrace();
            msg.setInfo("系统错误");
            msg.setCode("500");
        }
        return msg;
    }

    @PostMapping("/deleUser")
    @ResponseBody
    public Msg deleteUser(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            JSONArray userIdList = jsonObj.getJSONArray("arr");
            boolean b = false;
            for (int i = 0; i < userIdList.size(); i++) {
                User u = new User();
                u.setId(userIdList.getInteger(i));
                User user = userService.getUsers(u);
                if (user.getLevel() == 1) {
                    userService.delUser(userIdList.getInteger(i));
                } else {
                    b = true;
                }
            }
            if (b && userIdList.size() == 1) {
                msg.setInfo("管理员账户不可删除");
            } else {
                msg.setInfo("用户已删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setInfo("系统错误");
            msg.setCode("500");
        }
        return msg;
    }

    @PostMapping("/getKeysList")
    @ResponseBody
    public Msg getKeysList() {
        Msg msg = new Msg();
        List<Keys> list = keysService.getKeys();
        msg.setData(list);
        return msg;
    }

    @PostMapping("/LoadInfo")
    @ResponseBody
    public Msg LoadInfo(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonData = JSONObject.parseObject(data);
            Integer keyId = jsonData.getInteger("keyId");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", keyId);
            Keys key = keysService.selectKeys(keyId);
            com.ym.seeing.datasource.domain.Keys tempKey = new com.ym.seeing.datasource.domain.Keys();
            BeanUtils.copyProperties(key, tempKey);
            Integer ret = getSource.getInitType(tempKey);
            Long memory = imgService.getSourceMemory(keyId);
            jsonObject.put("isok", ret);
            jsonObject.put("storagetype", key.getStorageType());
            if (memory == null) {
                jsonObject.put("usedCapacity", 0);
            } else {
                jsonObject.put("usedCapacity", FileUtil.readableFileSize(memory));
            }
            msg.setData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("500");
        }
        return msg;
    }

    @PostMapping("/updateStorage")
    @ResponseBody
    public Msg updateStorage(@RequestParam(value = "data", defaultValue = "") String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        Integer id = jsonObj.getInteger("id");
        String accessKey = jsonObj.getString("AccessKey");
        String accessSecret = jsonObj.getString("AccessSecret");
        String endPoint = jsonObj.getString("Endpoint");
        String bucketName = jsonObj.getString("Bucketname");
        String requestAddress = jsonObj.getString("RequestAddress");
        Integer storageType = jsonObj.getInteger("storageType");
        String keyName = jsonObj.getString("keyname");
        Keys keys = new Keys();
        keys.setId(id);
        keys.setAccessKey(accessKey);
        keys.setAccessSecret(accessSecret);
        keys.setEndpoint(endPoint);
        keys.setBucketName(bucketName);
        keys.setRequestAddress(requestAddress);
        keys.setStorageType(storageType);
        keys.setKeyName(keyName);
        return keysService.updateKey(keys);
    }

    @PostMapping("/getStorageById")
    @ResponseBody
    public Msg getSelectKey(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        JSONObject jsonObj = JSONObject.parseObject(data);
        Integer keyId = jsonObj.getInteger("id");
        Keys keys = keysService.selectKeys(keyId);
        msg.setData(keys);
        return msg;
    }

    @PostMapping("/getSettingConfig")
    @ResponseBody
    public Msg getSettingConfig(@RequestParam(value = "data", defaultValue = "") String data) {
        final Msg msg = new Msg();
        final JSONObject jsonObject = new JSONObject();
        try {
            UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
            Config config = configService.getSourceType();
            SysConfig sysConfig = sysConfigService.getState();
            AppClient appClientData = appClientService.getAppClientData("app");
            uploadConfig.setUserMemory(Long.toString(Long.parseLong(uploadConfig.getUserMemory()) / 1024 / 1024));
            uploadConfig.setVisitorMemory(Long.toString(Long.parseLong(uploadConfig.getVisitorMemory()) / 1024 / 1024));
            uploadConfig.setFileSizeTourists(Long.toString(Long.parseLong(uploadConfig.getFileSizeTourists()) / 1024 / 1024));
            uploadConfig.setFileSizeUser(Long.toString(Long.parseLong(uploadConfig.getFileSizeUser()) / 1024 / 1024));
            jsonObject.put("uploadConfig", uploadConfig);
            jsonObject.put("config", config);
            jsonObject.put("sysConfig", sysConfig);
            jsonObject.put("appClient", appClientData);
            msg.setData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("操作失败");
        }
        return msg;
    }

    @PostMapping("/updateConfig")
    @ResponseBody
    public Msg updateConfig(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            UploadConfig uploadConfig = JSON.toJavaObject((JSON) jsonObject.get("uploadConfig"), UploadConfig.class);
            String vm = uploadConfig.getVisitorMemory();
            if ((Long.parseLong(vm) < -1) || Long.parseLong(vm) > 104857600
                    || Long.parseLong(uploadConfig.getFileSizeTourists()) < 0
                    || Long.parseLong(uploadConfig.getFileSizeTourists()) > 5120
                    || Long.parseLong(uploadConfig.getUserMemory()) < 0
                    || Long.parseLong(uploadConfig.getUserMemory()) > 1048576
                    || Long.parseLong(uploadConfig.getFileSizeUser()) < 0
                    || Long.parseLong(uploadConfig.getFileSizeUser()) > 5120) {
                msg.setInfo("你输入的值不正确");
                msg.setCode("500");
                return msg;
            }
            Config config = JSON.toJavaObject((JSON) jsonObject.get("config"), Config.class);
            SysConfig sysConfig = JSON.toJavaObject((JSON) jsonObject.get("sysConfig"), SysConfig.class);
            AppClient appClient = JSON.toJavaObject((JSON) jsonObject.get("appClient"), AppClient.class);
            if (Integer.parseInt(vm) == -1) {
                uploadConfig.setVisitorMemory("-1");
            } else {
                uploadConfig.setVisitorMemory(Long.toString(Long.parseLong(uploadConfig.getVisitorMemory()) * 1024 * 1024));
            }
            uploadConfig.setFileSizeTourists(Long.toString(Long.parseLong(uploadConfig.getFileSizeTourists()) * 1024 * 1024));
            uploadConfig.setUserMemory(Long.toString(Long.parseLong(uploadConfig.getUserMemory()) * 1024 * 1024));
            uploadConfig.setFileSizeUser(Long.toString(Long.parseLong(uploadConfig.getFileSizeUser()) * 1024 * 1024));
            uploadConfigService.setUpdateConfig(uploadConfig);
            configService.setSourceType(config);
            sysConfigService.setState(sysConfig);
            if (!appClient.getIsUse().equals("on")) {
                appClient.setIsUse("off");
            }
            appClientService.editAppClientData(appClient);
            msg.setInfo("配置保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            msg.setInfo("操作出现异常");
            msg.setCode("500");
        }
        return msg;
    }


    @PostMapping(value = "/getOrderConfig")
    @ResponseBody
    public Msg emailConfig() {
        final Msg msg = new Msg();
        EmailConfig emailConfig = null;
        ImgReview imgreview = null;
        try {
            final JSONObject jsonObject = new JSONObject();
            emailConfig = emailConfigService.getEmail();
            imgreview = imgreviewService.selectImgReviewByKey(1);
            jsonObject.put("emailConfig", emailConfig);
            jsonObject.put("imgreview", imgreview);
            msg.setData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("获取相关配置信息失败");
        }
        return msg;
    }


    @PostMapping("/updateEmailConfig")
    @ResponseBody
    public Msg updateEmail(@RequestParam(value = "data", defaultValue = "") String data) {
        final Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            EmailConfig emailConfig = JSON.toJavaObject(jsonObj, EmailConfig.class);
            if (null == emailConfig.getId() || null == emailConfig.getEmailName() || null == emailConfig.getEmailUrl() || null == emailConfig.getEmails()
                    || null == emailConfig.getEmailKey() || null == emailConfig.getPort() || null == emailConfig.getIsUsing()
                    || emailConfig.getEmailName().equals("") || emailConfig.getEmailUrl().equals("") || emailConfig.getEmails().equals("")
                    || emailConfig.getEmailKey().equals("") || emailConfig.getPort().equals("")) {
                msg.setCode("110400");
                msg.setInfo("各参数不能为空");
                return msg;
            }
            emailConfigService.updateEmail(emailConfig);
            msg.setInfo("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("保存过程出现错误");
        }
        return msg;
    }

}
