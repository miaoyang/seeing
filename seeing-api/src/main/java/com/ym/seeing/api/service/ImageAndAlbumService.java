package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Images;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 16:40
 * @Desc:
 */
public interface ImageAndAlbumService {
    Integer deleteImageAndAlbumByName(String imgName);

    List<Images> selectImgByAlbumKey(String key);

    void deleteImgAndAlbumByKey(String albumkey);
}
