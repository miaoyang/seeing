package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ym.seeing.api.domain.ImgReview;
import com.ym.seeing.api.mapper.ImgReviewMapper;
import com.ym.seeing.api.service.ImgReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 19:18
 * @Desc:
 */
@Service
@Slf4j
public class ImgReviewServiceImpl implements ImgReviewService {
    @Autowired
    private ImgReviewMapper imgReviewMapper;

    @Override
    public void updateByPrimaryKeySelective(ImgReview imgReview) {
        LambdaUpdateWrapper<ImgReview> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ImgReview::getId,imgReview.getId());
        if (imgReview.getAppId()!=null){
            updateWrapper.set(ImgReview::getAppId,imgReview.getAppId());
        }
        if (imgReview.getApiKey() !=null){
            updateWrapper.set(ImgReview::getApiKey,imgReview.getApiKey());
        }
        if (imgReview.getSecretKey() !=null){
            updateWrapper.set(ImgReview::getSecretKey,imgReview.getSecretKey());
        }
        if (imgReview.getUsing() != null){
            updateWrapper.set(ImgReview::getUsing,imgReview.getUsing());
        }
        if (imgReview.getCount() !=null){
            updateWrapper.set(ImgReview::getCount,imgReview.getCount());
        }
        imgReviewMapper.update(imgReview,updateWrapper);
    }

    @Override
    public ImgReview selectImgReviewByUsing(Integer using) {
        LambdaUpdateWrapper<ImgReview> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(ImgReview::getUsing,1);
        return imgReviewMapper.selectOne(lambdaUpdateWrapper);
    }

    @Override
    public ImgReview selectImgReviewByKey(Integer id) {
        return imgReviewMapper.selectById(id);
    }

    @Override
    public int insert(ImgReview imgReview) {
        return imgReviewMapper.insert(imgReview);
    }
}
