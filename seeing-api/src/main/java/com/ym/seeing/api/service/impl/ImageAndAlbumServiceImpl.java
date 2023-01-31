package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.ImageAndAlbum;
import com.ym.seeing.api.mapper.ImageAndAlbumMapper;
import com.ym.seeing.api.service.ImageAndAlbumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 16:41
 * @Desc:
 */
@Service
@Slf4j
public class ImageAndAlbumServiceImpl implements ImageAndAlbumService {
    @Autowired
    private ImageAndAlbumMapper imageAndAlbumMapper;

    @Override
    public Integer deleteImageAndAlbumByName(String imgName) {
        LambdaQueryWrapper<ImageAndAlbum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImageAndAlbum::getImgName,imgName);
        imageAndAlbumMapper.delete(wrapper);
        return null;
    }
}
