package com.ym.seeing.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ym.seeing.api.domain.*;
import com.ym.seeing.api.domain.vo.PageResultBean;
import com.ym.seeing.api.domain.vo.Result;
import com.ym.seeing.api.service.*;
import com.ym.seeing.api.util.UserUtil;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.MyProgress;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.utils.Base64Util;
import com.ym.seeing.core.utils.CodeUtil;
import com.ym.seeing.core.utils.FileUtil;
import com.ym.seeing.core.utils.TextUtil;
import com.ym.seeing.datasource.upload.GetSource;
import com.ym.seeing.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/23 16:43
 * @Desc:
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "yangmaio哈哈哈哈哈哈";
    }

    @RequestMapping(value = "/testpost", method = RequestMethod.POST)
    @ResponseBody
    public Result testPost() {
        return Result.ok("yangmiao");
    }

    @Autowired
    private ImgService imgService;
    @Autowired
    private IKeyService keysService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ImgReviewService imgreviewService;
    @Autowired
    private IUploadConfigService uploadConfigService;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private IAlbumService albumService;
    @Autowired
    private GetSource getSource;
    @Autowired
    private RedisService redisService;

    @PostMapping(value = "/overviewData")
    @ResponseBody
    public Msg overviewData(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();

        User user = UserUtil.getUser();
        user = userService.getUsers(user);
        JSONObject jsonObject = new JSONObject();
        UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
        ImgReview imgreview = imgreviewService.selectImgReviewByKey(1);
        ImgReview isImgreviewOK = imgreviewService.selectImgReviewByUsing(1);
        String ok = "false";
        jsonObject.put("myToken", user.getToken());
        jsonObject.put("myImgTotal", imgService.countImg(user.getId()));
        jsonObject.put("myAlbumTitle", albumService.selectAlbumCount(user.getId()));
        long memory = Long.parseLong(user.getMemory());
        Long usermemory =
                imgService.getUserMemory(user.getId()) == null
                        ? 0L
                        : imgService.getUserMemory(user.getId());
        if (memory == 0) {
            jsonObject.put("myMemory", "无容量");
        } else {
            Double aDouble =
                    Double.valueOf(String.format("%.2f", (((double) usermemory / (double) memory) * 100)));
            if (aDouble >= 999) {
                jsonObject.put("myMemory", 999);
            } else {
                jsonObject.put("myMemory", aDouble);
            }
        }
        jsonObject.put("myMemorySum", FileUtil.readableFileSize(memory));
        if (user.getLevel() > 1) {
            ok = "true";
            jsonObject.put("imgTotal", imgService.countImg(null));
            jsonObject.put("userTotal", userService.getUserTotal());
            jsonObject.put("ViolationImgTotal", imgreview.getCount());
            jsonObject.put("ViolationSwitch", isImgreviewOK == null ? 0 : isImgreviewOK.getId());
            jsonObject.put("VisitorUpload", uploadConfig.getIsUpdate());
            jsonObject.put(
                    "VisitorMemory",
                    FileUtil.readableFileSize(Long.valueOf(uploadConfig.getVisitorMemory()))); // 访客共大小
            if (uploadConfig.getIsUpdate() != 1) {
                jsonObject.put("VisitorUpload", 0);
                jsonObject.put("VisitorProportion", 100.00);
                jsonObject.put("VisitorMemory", "禁用");
            } else {
                Long temp = imgService.getUserMemory(0) == null ? 0 : imgService.getUserMemory(0);
                jsonObject.put("UsedMemory", (temp == null ? 0 : FileUtil.readableFileSize(temp)));
                if (Long.parseLong(uploadConfig.getVisitorMemory()) == 0) {
                    jsonObject.put("VisitorProportion", 100.00);
                } else if (Long.parseLong(uploadConfig.getVisitorMemory()) == -1) {
                    jsonObject.put("VisitorProportion", 0);
                    jsonObject.put("VisitorMemory", "无限");
                } else {
                    double sum = Double.parseDouble(uploadConfig.getVisitorMemory());
                    Double aDouble = Double.valueOf(String.format("%.2f", ((double) temp / sum) * 100));
                    if (aDouble >= 999) {
                        jsonObject.put("VisitorProportion", 999);
                    } else {
                        jsonObject.put("VisitorProportion", aDouble);
                    }
                }
            }
        }
        jsonObject.put("ok", ok);
        msg.setData(jsonObject);
        return msg;
    }

    @PostMapping(value = "/SpaceExpansion")
    @ResponseBody
    public Msg spaceExpansion(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        JSONObject jsonObject = JSONObject.parseObject(data);
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        user = userService.getUsers(user);
        if (user.getIsOk() == 0) {
            msg.setCode("100403");
            msg.setInfo("你暂时无法使用此功能");
            return msg;
        }
        if (null == user) {
            msg.setCode("100405");
            msg.setInfo("用户信息不存在");
            return msg;
        } else {
            long sizes = 0;
            Code code = codeService.selectCodeKey(jsonObject.getString("code"));
            if (null == code) {
                msg.setCode("100404");
                msg.setInfo("扩容码不存在,请重新填写");
                return msg;
            }
            Long userMemory = Long.valueOf(user.getMemory());
            sizes = Long.valueOf(code.getValue()) + userMemory;
            User newMemoryUser = new User();
            newMemoryUser.setMemory(Long.toString(sizes));
            newMemoryUser.setId(user.getId());
            userService.userSetMemory(newMemoryUser, jsonObject.getString("code"));
            msg.setInfo("你已成功扩容" + FileUtil.readableFileSize(sizes));
            return msg;
        }
    }

    @PostMapping(value = {"/setImgFileName", "/client/setImgFileName"})
    @ResponseBody
    public Msg setImgFileName(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String name = jsonObject.getString("name");
            String imgname = jsonObject.getString("imgname");
            Images images = new Images();
            images.setIdName(name.replaceAll(" ", ""));
            images.setImgName(imgname);
            imgService.updateImgByImgName(images);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("500");
        }
        return msg;
    }

    @PostMapping("/getRecently")
    @ResponseBody
    public Msg getRecently() {
        Msg msg = new Msg();
        JSONObject jsonObject = new JSONObject();
        try {
            User user = UserUtil.getUser();
            user = userService.getUsers(user);
            log.debug("current user: {}",user.toString());
            if (user.getLevel() > 1) {
                jsonObject.put("RecentlyUser", userService.getRecentlyUser());
                jsonObject.put("RecentlyUploaded", imgService.getRecentlyUploaded(user.getId()));
            } else {
                jsonObject.put("RecentlyUploaded", imgService.getRecentlyUploaded(user.getId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setInfo("系统内部错误");
            msg.setCode("500");
            return msg;
        }
        msg.setData(jsonObject);
        return msg;
    }

    @PostMapping("/updateToken")
    @ResponseBody
    public Msg updateToken() {
        Msg msg = new Msg();
        try {
            Subject subject = SecurityUtils.getSubject();
            User user = (User) subject.getPrincipal();
            User u = new User();
            u.setId(user.getId());
            u.setToken(TextUtil.get32UUID());
            userService.changeUser(u);
            user.setToken(u.getToken());
            msg.setData(u.getToken());
            msg.setInfo("Token更新成功,即可生效");
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("500");
            msg.setInfo("Token更新失败");
        }
        return msg;
    }

    @PostMapping("/getYyyy")
    @ResponseBody
    public Msg getYyyy() {
        Msg msg = new Msg();
        Subject subject = SecurityUtils.getSubject();
        User u = (User) subject.getPrincipal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("allYyyy", imgService.getyyyy(null));
        jsonObject.put("userYyyy", imgService.getyyyy(u.getId()));
        msg.setData(jsonObject);
        return msg;
    }

    @PostMapping("/getChart")
    @ResponseBody
    public Msg getChart(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        JSONObject jsonObject = JSONObject.parseObject(data);
        String yyyy = jsonObject.getString("yyyy");
        Integer type = jsonObject.getInteger("type");
        Subject subject = SecurityUtils.getSubject();
        User u = (User) subject.getPrincipal();
        List<Images> list = null;
        if (u.getLevel() > 1) {
            if (type == 2) {
                Images images = new Images();
                images.setYyyy(yyyy);
                list = imgService.countByUpdateTime(images);
            } else {
                Images images = new Images();
                images.setYyyy(yyyy);
                images.setUserId(u.getId());
                list = imgService.countByUpdateTime(images);
            }
        } else {
            Images images = new Images();
            images.setYyyy(yyyy);
            images.setUserId(u.getId());
            list = imgService.countByUpdateTime(images);
        }
        JSONArray json =
                JSONArray.parseArray(
                        "[{\"id\":1,\"monthNum\":\"一月\",\"countNum\":0},{\"id\":2,\"monthNum\":\"二月\",\"countNum\":0},{\"id\":3,\"monthNum\":\"三月\",\"countNum\":0},{\"id\":4,\"monthNum\":\"四月\",\"countNum\":0},{\"id\":5,\"monthNum\":\"五月\",\"countNum\":0},{\"id\":6,\"monthNum\":\"六月\",\"countNum\":0},{\"id\":7,\"monthNum\":\"七月\",\"countNum\":0},{\"id\":8,\"monthNum\":\"八月\",\"countNum\":0},{\"id\":9,\"monthNum\":\"九月\",\"countNum\":0},{\"id\":10,\"monthNum\":\"十月\",\"countNum\":0},{\"id\":11,\"monthNum\":\"十一月\",\"countNum\":0},{\"id\":12,\"monthNum\":\"十二月\",\"countNum\":0}]");
        for (Images images : list) {
            for (int i = 0; i < json.size(); i++) {
                JSONObject jobj = json.getJSONObject(i);
                if (jobj.getInteger("id").equals(images.getMonthNum())) {
                    jobj.put("monthNum", TextUtil.getChinaMonth(images.getMonthNum()));
                    jobj.put("countNum", images.getCountNum());
                }
            }
        }
        msg.setData(json);
        return msg;
    }

    @PostMapping("/getStorage")
    @ResponseBody
    public Msg getStorage() {
        Msg msg = new Msg();
        List<Keys> storage = keysService.getStorage();
        msg.setData(storage);
        return msg;
    }

    @PostMapping("/getStorageName")
    @ResponseBody
    public Msg getStorageName() {
        Msg msg = new Msg();
        List<Keys> storage = keysService.getStorageName();
        msg.setData(storage);
        return msg;
    }

    @PostMapping(value = {"/selectPhoto", "/client/selectPhoto"})
    @ResponseBody
    public Msg selectPhoto(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        JSONObject jsonObj = JSONObject.parseObject(data);
        Integer pageNum = jsonObj.getInteger("pageNum");
        Integer pageSize = jsonObj.getInteger("pageSize");
        Integer source = jsonObj.getInteger("source");
        String starttime = jsonObj.getString("starttime");
        String stoptime = jsonObj.getString("stoptime");
        String selectUserType = jsonObj.getString("selectUserType");
        String username = jsonObj.getString("username");
        Integer selecttype = jsonObj.getInteger("selecttype");
        boolean violation = jsonObj.getBoolean("violation");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime != null) {
            try {
                Date date1 = format.parse(starttime);
                Date date2 = format.parse(stoptime == null ? format.format(new Date()) : stoptime);
                int compareTo = date1.compareTo(date2);
                System.out.println(compareTo);
                if (compareTo == 1) {
                    msg.setCode("110500");
                    msg.setInfo("起始日期不能大于结束日期");
                    return msg;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                msg.setCode("110500");
                msg.setInfo("您输入的日期不正确");
                return msg;
            }
        }
        Images img = new Images();
        PageHelper.startPage(pageNum, pageSize);
        if (violation) {
            img.setViolation("true");
        }
        img.setStartTime(starttime);
        img.setStopTime(stoptime);
        if (subject.hasRole("admin")) {
            img.setSource(source);
            if (selectUserType.equals("me")) {
                img.setUserId(user.getId());
                img.setUserName(null);
                img.setSelectType(null);
            } else {
                img.setUserName(username);
                img.setSelectType(selecttype);
            }
        } else {
            img.setUserId(user.getId());
        }
        List<Images> images = imgService.selectImages(img);
        PageInfo<Images> rolePageInfo = new PageInfo<>(images);
        PageResultBean<Images> pageResultBean =
                new PageResultBean<>(rolePageInfo.getTotal(), rolePageInfo.getList());
        msg.setData(pageResultBean);
        return msg;
    }

    @PostMapping(value = "/getUserInfo")
    @ResponseBody
    public Msg getUserInfo() {
        Msg msg = new Msg();
        try {

            User user = UserUtil.getUser();
            User u = new User();
            u.setId(user.getId());
            User userInfo = userService.getUsers(u);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userInfo.getUserName());
            jsonObject.put("email", userInfo.getEmail());
            msg.setData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("操作失败");
        }
        return msg;
    }

    @PostMapping("/setUserInfo")
    @ResponseBody
    public Msg setUserInfo(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String username = jsonObject.getString("username");
            String email = jsonObject.getString("email");
            String password = jsonObject.getString("password");
            Subject subject = SecurityUtils.getSubject();
            User u = (User) subject.getPrincipal();
            User user = new User();
            if (!TextUtil.checkEmail(email)) {
                msg.setCode("110403");
                msg.setInfo("邮箱格式不正确");
                return msg;
            }
            String regex = "^\\w+$";
            if (username.length() > 20 || !username.matches(regex)) {
                msg.setCode("110403");
                msg.setInfo("用户名不得超过20位字符");
                return msg;
            }
            if (subject.hasRole("admin")) {
                User userOld = new User();
                userOld.setId(u.getId());
                User userInfo = userService.getUsers(userOld);
                if (!userInfo.getUserName().equals(username)) {
                    Integer countusername = userService.countUsername(username);
                    if (countusername == 1 || !UserUtil.checkSysName(username)) {
                        msg.setCode("110406");
                        msg.setInfo("此用户名已存在");
                        return msg;
                    } else {
                        user.setUserName(username);
                    }
                }
                if (!userInfo.getEmail().equals(email)) {
                    Integer countmail = userService.countMail(email);
                    if (countmail == 1) {
                        msg.setCode("110407");
                        msg.setInfo("此邮箱已被注册");
                        return msg;
                    } else {
                        user.setEmail(email);
                    }
                }
                user.setPassword(Base64Util.encryptBASE64(password.getBytes()));
                user.setUid(u.getUid());
            } else {
                user.setPassword(Base64Util.encryptBASE64(password.getBytes()));
                user.setUid(u.getUid());
            }
            userService.change(user);
            msg.setInfo("信息修改成功，请重新登录");
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("服务执行异常，请稍后再试");
        }
        return msg;
    }

    @PostMapping({"/deleImages", "/client/deleImages"})
    @ResponseBody
    public Msg deleteImages(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        JSONObject jsonObj = JSONObject.parseObject(data);
        String images = jsonObj.getString("images");
        String uuid = jsonObj.getString("uuid");
        if (images == null) {
            msg.setCode("404");
            msg.setInfo("为获取到图像信息");
            return msg;
        }
        String[] split = images.split(",");
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if (null == user) {
            msg.setCode("500");
            msg.setInfo("当前用户信息不存在");
            return msg;
        }
        if (split.length == 0) {
            msg.setCode("404");
            msg.setInfo("为获取到图像信息");
            return msg;
        }
        List<Integer> imgIds = new ArrayList<Integer>();
        for (int i = 0; i < split.length; i++) {
            Integer imgId = Integer.valueOf(split[i]);
            Images image = imgService.selectByImgUid(String.valueOf(imgId));
            if (!subject.hasRole("admin")) {
                if (!image.getUserId().equals(user.getId())) {
                    break;
                }
            }
            imgIds.add(imgId);
        }
        if (imgIds.isEmpty()) {
            msg.setCode("110404");
        } else {
            imgService.deleteImg(uuid,imgIds.stream().toArray(Integer[]::new));
            msg.setCode("200");
        }
        return msg;
    }

    @PostMapping({"/GetDelprogress", "/client/GetDelprogress"})
    @ResponseBody
    public Msg GetDelprogress(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            String uuid = jsonObj.getString("uuid");
            JSONObject jsonObject = new JSONObject();
            MyProgress myProgress = null;
            try{
                myProgress = JSON.parseObject(redisService.getCacheObject(uuid).toString(), MyProgress.class);
            }catch (Exception e){ }
            msg.setCode("110666");
            msg.setInfo("正在执行");
            if (null == myProgress) {
                jsonObject.put("succ", 0);
                jsonObject.put("errorlist", new ArrayList<String>());
                jsonObject.put("oklist", new ArrayList<Long>());
                jsonObject.put("delover", false);
            } else {
                jsonObject.put("succ", myProgress.getDelSuccessCount());
                jsonObject.put("errorlist", myProgress.getDelErrorImgListt());
                jsonObject.put("oklist", myProgress.getDelSuccessImgList());
                jsonObject.put("delover", myProgress.getDelOCT());
                if(myProgress.getDelOCT()==1){
                    System.out.println("循环遍历的值："+myProgress.getDelOCT());
                    msg.setCode("100");
                }
            }
            msg.setData(jsonObject);
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("正在执行过程中发生错误");
            return msg;
        }
    }
}
