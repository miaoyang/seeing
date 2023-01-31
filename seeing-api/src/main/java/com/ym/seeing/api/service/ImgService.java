package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Images;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 19:09
 * @Desc:
 */
public interface ImgService {

    void updateImgByImgName(Images images);

    Integer insertImgData(Images images);

    List<Images> selectImgUrlByMD5(String md5Key);

    Images selectByImgUid(String imgUid);

    List<Integer> deleteImg(String uuid, Integer... imgIds);

    Integer deleteByImgId(Integer imgId);
}
