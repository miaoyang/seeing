package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Images;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 15:13
 * @Desc:
 */
public interface ImgDataService {
    Long selectUserMemoryById(Integer id);

    Images selectByImgUid(String imgUid);
}
