package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Images;
import com.ym.seeing.api.domain.ImgTemp;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 14:59
 * @Desc: 临时图片
 */
public interface ImgTempService {
    List<Images> selectDelImgUidList(String dataTime);

    Integer delImgAndExp(String imgUid);

    Integer insertImgExp(ImgTemp imgDataExp);
}
