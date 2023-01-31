package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.ImgReview;
import io.swagger.models.auth.In;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 19:16
 * @Desc:
 */
public interface ImgReviewService {

    public void updateByPrimaryKeySelective(ImgReview imgReview);

    ImgReview selectImgReviewByUsing(Integer using);
}
