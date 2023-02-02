package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Images;
import com.ym.seeing.core.domain.User;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 19:09
 * @Desc:
 */
public interface ImgService {

    List<Images> selectImages(Images images);

    void updateImgByImgName(Images images);

    Integer insertImgData(Images images);

    List<Images> selectImgUrlByMD5(String md5Key);

    Images selectByImgUid(String imgUid);

    List<Integer> deleteImg(String uuid, Integer... imgIds);

    Integer deleteByImgId(Integer imgId);

    Integer countImg(Integer id);

    Long getUserMemory(Integer id);

    Long getSourceMemory(Integer keyId);

    List<Images> getRecentlyUploaded(Integer id);

    List<String> getyyyy(Integer id);

    List<Images> countByUpdateTime(Images images);
}
