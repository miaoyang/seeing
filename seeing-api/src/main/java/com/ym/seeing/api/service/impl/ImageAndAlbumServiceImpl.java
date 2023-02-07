package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ym.seeing.api.domain.ImageAndAlbum;
import com.ym.seeing.api.domain.Images;
import com.ym.seeing.api.mapper.AlbumMapper;
import com.ym.seeing.api.mapper.ImageAndAlbumMapper;
import com.ym.seeing.api.service.ImageAndAlbumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Autowired
    private AlbumMapper albumMapper;

    @Override
    public Integer deleteImageAndAlbumByName(String imgName) {
        LambdaQueryWrapper<ImageAndAlbum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImageAndAlbum::getImgName,imgName);
        return imageAndAlbumMapper.delete(wrapper);
    }

    @Override
    public List<Images> selectImgByAlbumKey(String key) {
        return albumMapper.selectImgByAlbumKey(key);
    }

    @Override
    public void deleteImgAndAlbumByKey(String albumkey) {
        LambdaQueryWrapper<ImageAndAlbum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImageAndAlbum::getAlbumKey,albumkey);
        imageAndAlbumMapper.delete(wrapper);
    }
}
