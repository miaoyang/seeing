package com.ym.seeing.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ym.seeing.api.domain.ImgReview;
import com.ym.seeing.api.service.ImgReviewService;
import com.ym.seeing.core.domain.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/31 21:05
 * @Desc:
 */
@Controller
@RequestMapping("/admin/root")
public class ImgReviewerController {
    @Autowired
    private ImgReviewService imgReviewService;

    @PostMapping("/updateimgReviewConfig")
    @ResponseBody
    public Msg updateImgReviewConfig(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObj = JSONObject.parseObject(data);
            ImgReview imgReview = JSON.toJavaObject(jsonObj, ImgReview.class);
            if (imgReview.getId() == 1) {
                if (imgReview.getCount() == null || imgReview.getUsing() != null || imgReview.getSecretKey() != null ||
                        imgReview.getApiKey() != null || imgReview.getAppId() != null) {
                    msg.setInfo("各项参数不能为空");
                    msg.setCode("110400");
                    return msg;
                }
            } else {
                if (null == imgReview.getId() || null == imgReview.getApiKey() || null == imgReview.getUsing()
                        || imgReview.getApiKey().equals("")
                ) {
                    msg.setCode("110400");
                    msg.setInfo("各参数不能为空");
                    return msg;
                }
            }
            if (imgReviewService.selectImgReviewByKey(imgReview.getId()) !=null){
                imgReviewService.insert(imgReview);
            }else {
                imgReviewService.updateByPrimaryKeySelective(imgReview);
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("保存过程出现错误");
        }
        return msg;
    }
}
