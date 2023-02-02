package com.ym.seeing.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ym.seeing.api.domain.Images;
import com.ym.seeing.api.domain.ImgData;
import com.ym.seeing.api.domain.Keys;
import com.ym.seeing.api.mapper.ImgDataMapper;
import com.ym.seeing.api.mapper.ImgMapper;
import com.ym.seeing.api.service.IKeyService;
import com.ym.seeing.api.service.ImageAndAlbumService;
import com.ym.seeing.api.service.ImgService;
import com.ym.seeing.api.service.ImgTempService;
import com.ym.seeing.datasource.upload.GetSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 19:10
 * @Desc:
 */
@Service
@Slf4j
public class ImgServiceImpl implements ImgService {
    @Autowired
    private ImgMapper imgMapper;
    @Autowired
    private IKeyService keyService;
    @Autowired
    private GetSource getSource;
    @Autowired
    private ImageAndAlbumService imageAndAlbumService;
    @Autowired
    private ImgTempService imgTempService;
    @Autowired
    private ImgDataMapper imgDataMapper;

    @Override
    public List<Images> selectImages(Images images) {
        return imgMapper.selectImages(images);
    }

    @Override
    public void updateImgByImgName(Images images) {
        LambdaUpdateWrapper<Images> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (images.getAbnormal() != null) {
            lambdaUpdateWrapper.set(Images::getAbnormal, images.getAbnormal());
        }
        if (images.getExplains() != null) {
            lambdaUpdateWrapper.set(Images::getExplains, images.getExplains());
        }
        if (!StrUtil.isBlank(images.getIdName())) {
            lambdaUpdateWrapper.set(Images::getIdName, images.getIdName());
        }
        lambdaUpdateWrapper.eq(Images::getImgName, images.getImgName());
        imgMapper.update(images, lambdaUpdateWrapper);
    }

    @Override
    public Integer insertImgData(Images images) {
        return imgMapper.insert(images);
    }

    @Override
    public List<Images> selectImgUrlByMD5(String md5Key) {
        LambdaQueryWrapper<Images> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Images::getMd5Key, md5Key);
        return imgMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public Images selectByImgUid(String imgUid) {
        LambdaQueryWrapper<Images> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Images::getImgUid, imgUid);
        return imgMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public List<Integer> deleteImg(String uuid, Integer... imgIds) {
        List<Integer> idList = new ArrayList<>();
        for (int i = 0; i < imgIds.length; i++) {
            Images images = this.selectByImgUid(String.valueOf(imgIds[i]));
            Keys keys = keyService.selectKeys(images.getSource());
            com.ym.seeing.datasource.domain.Images imageDataSource = new com.ym.seeing.datasource.domain.Images();
            BeanUtils.copyProperties(images, imageDataSource);
            boolean b = getSource.deleteImg(keys.getStorageType(), keys.getId(), imageDataSource);
            log.debug("删除图像: images={} isSuccess={}", imageDataSource, b);
            // 删除其他图像
            imageAndAlbumService.deleteImageAndAlbumByName(images.getImgName());
            imgTempService.delImgAndExp(images.getImgUid());
            this.deleteByImgId(images.getId());
            idList.add(images.getId());
        }
        return idList;
    }

    @Override
    public Integer deleteByImgId(Integer imgId) {
        LambdaQueryWrapper<Images> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Images::getId, imgId);
        return imgMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public Integer countImg(Integer id) {
        LambdaQueryWrapper<ImgData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImgData::getId, id);
        return imgDataMapper.selectCount(wrapper);
    }

    @Override
    public Long getUserMemory(Integer id) {
        QueryWrapper<ImgData> wrapper = new QueryWrapper<>();
        wrapper.select("select IFNULL(sum(sizes),0) as sizes").eq("user_id", id);
        ImgData imgData = imgDataMapper.selectOne(wrapper);
        Long sizes = 0L;
        if (imgData != null && Long.parseLong(imgData.getSizes()) >= 0) {
            sizes = Long.parseLong(imgData.getSizes());
        }
        return sizes;
    }

    @Override
    public Long getSourceMemory(Integer keyId) {
        return imgMapper.getSourceMemory(keyId);
    }

    @Override
    public List<Images> getRecentlyUploaded(Integer id) {
        return imgMapper.getRecentlyUploaded(id);
    }

    @Override
    public List<String> getyyyy(Integer id) {
        return imgMapper.getyyyy(id);
    }

    @Override
    public List<Images> countByUpdateTime(Images images) {
        return imgMapper.countByUpdateTime(images);
    }
}
