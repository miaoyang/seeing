package com.ym.seeing.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;
import com.ym.seeing.api.domain.*;
import com.ym.seeing.api.service.*;
import com.ym.seeing.api.util.UserUtil;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.utils.*;
import com.ym.seeing.datasource.domain.ReturnImage;
import com.ym.seeing.datasource.upload.GetSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 19:26
 * @Desc:
 */
@Service
@Slf4j
public class UploadServiceImpl implements IUploadService {
    @Autowired
    private IUploadConfigService uploadConfigService;
    @Autowired
    private IKeyService keyService;
    @Autowired
    private ImgService imgService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ImgReviewService imgReviewService;
    @Autowired
    private ImgTempService imgTempService;
    @Autowired
    private ImgDataService imgDataService;
    @Autowired
    private GetSource getSource;

    /**上传用户或游客的所属分组**/
    private static Group group;
    /**上传用户或者游客的分配容量**/
    private static Long memory;
    /**用户或者游客下可使用的总容量**/
    private static Long TotleMemory;
    /**用户或者游客已经用掉的总容量**/
    private static Long UsedTotalMemory;
    /**路径**/
    private static String updatePath = "tourist";

    @Override
    public Msg uploadForLoc(HttpServletRequest request, MultipartFile multipartFile, Integer setDay, String imgUrl) {
        Msg msg = new Msg();
        try {
            String imageFileName = "未命名图像";
            JSONObject jsonObject = new JSONObject();
            UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
            String userIp = IPUtil.getIpAddr(request);
            User u = UserUtil.getUser();
            if (null != u) {
                u = userService.getUsers(u);
            }
            Integer sourceKeyId = 0;
            String md5key = null;
            FileInputStream fis = null;
            File file = null;
            if (imgUrl == null) {
                imageFileName = multipartFile.getOriginalFilename();
                file = FileUtil.changeFile_new(multipartFile);
            } else {
                imageFileName = "URL转存图像";
                Msg imgData = uploadForURL(request, imgUrl);
                if (imgData.getCode().equals("200")) {
                    file = new File((String) imgData.getData());
                } else {
                    return imgData;
                }
            }
            String imgUid = TextUtil.get32UUID();
            Msg msg1 = updateImgCheck(u, uploadConfig);
            if (!msg1.getCode().equals("300")) {
                return msg1;
            }
            sourceKeyId = group.getKeyId();
            Keys key = keyService.selectKeys(sourceKeyId);
            Long tmp = (memory == -1 ? -2 : UsedTotalMemory);
            if (tmp >= memory) {
                msg.setCode("4005");
                msg.setInfo(u == null ? "游客空间已用尽" : "您的可用空间不足");
                return msg;
            }
            if (file.length() > TotleMemory) {
                log.error("文件大小：" + file.length());
                log.error("最大限制：" + TotleMemory);
                msg.setCode("4006");
                msg.setInfo("图像超出系统限制大小");
                return msg;
            }
            try {
                fis = new FileInputStream(file);
                md5key = DigestUtils.md5Hex(fis);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("未获取到图片的MD5,成成UUID");
            }
            Msg fileMiME = TypeDictUtil.fileMiME(file);
            if (!fileMiME.getCode().equals("200")) {
                msg.setCode("4000");
                msg.setInfo(fileMiME.getInfo());
                return msg;
            }
            if (StrUtil.isEmpty(md5key)) {
                md5key = TextUtil.get32UUID();
            }
            String prefix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            // 判断黑名单
            if (uploadConfig.getBlackList() != null) {
                String[] iparr = uploadConfig.getBlackList().split(";");
                for (String s : iparr) {
                    if (s.equals(userIp)) {
                        file.delete();
                        msg.setCode("4003");
                        msg.setInfo("你暂时不能上传");
                        return msg;
                    }
                }
            }
            // 先存数据库
            Images imgObj = new Images();
            String imgnameEd = null;
            Map<Map<String, String>, File> map = new HashMap<>();
            if (file.exists()) {
                Map<String, String> m1 = new HashMap<>();
                String shortUuid_y = TextUtil.getShortUuid();
                m1.put("prefix", prefix);
                m1.put("name", shortUuid_y);
                map.put(m1, file);
                imgnameEd = updatePath + "/" + shortUuid_y + "." + prefix;
                imgObj.setImgName(imgnameEd);
                if (key.getStorageType().equals(5)) {
                    imgObj.setImgUrl(key.getRequestAddress() + "/ota/" + imgnameEd);
                } else {
                    imgObj.setImgUrl(key.getRequestAddress() + "/" + imgnameEd);
                }
                imgObj.setSizes(Long.toString(file.length()));
            }else{
                msg.setInfo("未获取到指定图像:110503");
                msg.setCode("110503");
                return msg;
            }
            imgObj.setUpdateTime(DateUtil.dateToStr(new Date()));
            imgObj.setSource(key.getId());
            imgObj.setUserId(u == null ? 0 : u.getId());
            if (setDay == 1 || setDay == 3 || setDay == 7 || setDay == 30) {
                imgObj.setImgType(1);
                ImgTemp imgDataExp = new ImgTemp();
                imgDataExp.setDelTime(DateUtil.plusDay(setDay));
                imgDataExp.setImgUid(imgUid);
                imgTempService.insertImgExp(imgDataExp);
            } else {
                imgObj.setImgType(0);
            }
            imgObj.setAbnormal(userIp);
            imgObj.setMd5Key(md5key);
            imgObj.setImgUid(imgUid);
            imgObj.setFormat(fileMiME.getData().toString());
            imgObj.setIdName(imageFileName);
            Integer insertRet = imgService.insertImgData(imgObj);
            if (insertRet == 0) {
                Images imaOBJ = new Images();
                imaOBJ.setMd5Key(md5key);
                imaOBJ.setUserId(u == null ? 0 : u.getId());
                List<Images> images = imgService.selectImgUrlByMD5(md5key);
                if (images.size() > 0) {
                    jsonObject.put("url", images.get(0).getImgUrl());
                    jsonObject.put("name", file.getName());
                    jsonObject.put("imguid", images.get(0).getImgUid());
                    msg.setData(jsonObject);
                    return msg;
                } else {
                    msg.setInfo("未获取到指定图像");
                    msg.setCode("110501");
                    return msg;
                }
            }
            long stime = System.currentTimeMillis();
            ReturnImage returnImage = getSource.storageSource(key.getStorageType(), map, updatePath, key.getId());
            if (returnImage.getCode().equals("200")) {
                long etime = System.currentTimeMillis();
                PrintUtil.Normal("上传图片所用总时长：" + String.valueOf(etime - stime) + "ms");
                jsonObject.put("url", imgObj.getImgUrl());
                jsonObject.put("name", imgObj.getImgName());
                jsonObject.put("imguid", imgObj.getImgUid());
            } else {
                msg.setCode("5001");
                msg.setInfo("上传服务内部错误");
                return msg;
            }
            file.delete();
            msg.setData(jsonObject);
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            msg.setInfo("上传时发生了一些错误");
            msg.setCode("110500");
            return msg;
        }
    }

    public static Msg uploadForURL(HttpServletRequest request, String imgurl) {
        Msg msg = new Msg();
        try {
            String shortUid = TextUtil.getShortUuid();
            String savePath =
                    request.getSession().getServletContext().getRealPath("/")
                            + File.separator
                            + "hellohaotmp"
                            + File.separator;
            Map<String, Object> bl = ImgUrlUtil.downLoadFromUrl(imgurl, shortUid, savePath);
            if ((Boolean) bl.get("res") == true) {
                msg.setCode("200");
                msg.setData(bl.get("imgPath"));
                return msg;
            } else {
                if (bl.get("StatusCode").equals("110403")) {
                    msg.setInfo("该链接非图像文件，无法上传");
                } else {
                    msg.setInfo("该链接暂时无法上传");
                }
                msg.setCode("500");
            }
        } catch (Exception e) {
            msg.setCode("500");
            msg.setInfo("获取资源失败");
        }

        return msg;
    }

    /**
     * 判断用户 或 游客 当前上传图片的一系列校验
     * @param user
     * @param uploadConfig
     * @return
     */
    private Msg updateImgCheck(User user, UploadConfig uploadConfig) {
        Msg msg = new Msg();
        java.text.DateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            if (user == null) {
                if (uploadConfig.getIsUpdate() != 1) {
                    msg.setCode("1000");
                    msg.setInfo("系统已禁用游客上传");
                    return msg;
                }
                updatePath = "tourist";
                group = SourceServiceImpl.GetSource(null);
                // 单位 B 游客设置总量
                memory = Long.valueOf(uploadConfig.getVisitorMemory());
                // 单位 B  游客单文件大小
                TotleMemory = Long.valueOf(uploadConfig.getFileSizeTourists());
                UsedTotalMemory = imgDataService.selectUserMemoryById(0);

            } else {
                if (uploadConfig.getUserClose() != 1) {
                    msg.setCode("1001");
                    msg.setInfo("系统已禁用上传功能");
                    return msg;
                }
                updatePath = user.getUserName();
                group = SourceServiceImpl.GetSource(user.getId());
                memory = Long.valueOf(user.getMemory()); // 单位 B  *1024*1024
                TotleMemory = Long.valueOf(uploadConfig.getFileSizeUser()); // 单位 B
                UsedTotalMemory = imgDataService.selectUserMemoryById(user.getId());
            }
            if (uploadConfig.getUrlType() == 2) {
                updatePath = dateFormat.format(new Date());
            }
            msg.setCode("300");
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("500");
        }
        return msg;
    }

    /**
     * 鉴别非法图像
     * @param images
     */
    private synchronized void checkImageLegal(Images images) {
        log.error("非法图像鉴别进程启动");
        ImgReview imgReview = null;
        try {
            imgReview = imgReviewService.selectImgReviewByUsing(1);
        } catch (Exception e) {
            log.error("获取鉴别程序的时候发生错误");
            e.printStackTrace();
        }
        if (null != imgReview) {
            legalImageCheckForBaiDu(imgReview, images);
        }
    }

    private void legalImageCheckForBaiDu(ImgReview imgreview, Images images) {
        log.error("非法图像鉴别进程启动-BaiDu");
        if (imgreview.getUsing() == 1) {
            try {
                AipContentCensor client = new AipContentCensor(
                                imgreview.getAppId(),
                                imgreview.getApiKey(),
                                imgreview.getSecretKey()
                );
                client.setConnectionTimeoutInMillis(5000);
                client.setSocketTimeoutInMillis(30000);
                JSONObject res = client.antiPorn(images.getImgUrl());
                res = client.imageCensorUserDefined(images.getImgUrl(), EImgType.URL, null);
                log.error("返回的鉴黄json:" + res.toString());

                JSONArray jsonArray = JSON.parseArray("[" + res.toString() + "]");
                for (Object o : jsonArray) {
                    com.alibaba.fastjson.JSONObject jsonObject =
                            (com.alibaba.fastjson.JSONObject) o;
                    JSONArray data = jsonObject.getJSONArray("data");
                    Integer conclusionType = jsonObject.getInteger("conclusionType");
                    if (conclusionType != null) {
                        if (conclusionType == 2) {
                            for (Object datum : data) {
                                JSONObject imgData = (JSONObject) datum;
                                if (imgData.getInt("type") == 1) {
                                    Images img = new Images();
                                    img.setImgName(images.getImgName());
                                    img.setViolation("1[1]");
                                    imgService.updateImgByImgName(img);

                                    ImgReview imgv = new ImgReview();
                                    imgv.setId(1);
                                    Integer count = imgreview.getCount();
                                    log.error("违法图片总数：" + count);
                                    imgv.setCount(count + 1);
                                    imgReviewService.updateByPrimaryKeySelective(imgv);
                                    log.error("存在非法图片，进行处理操作");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("图像鉴黄线程执行过程中出现异常");
                e.printStackTrace();
            }
        }
    }

}
