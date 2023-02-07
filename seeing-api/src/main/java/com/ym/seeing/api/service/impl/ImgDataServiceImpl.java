package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ym.seeing.api.domain.Images;
import com.ym.seeing.api.domain.ImgData;
import com.ym.seeing.api.mapper.ImgDataMapper;
import com.ym.seeing.api.mapper.ImgMapper;
import com.ym.seeing.api.service.ImgDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 15:14
 * @Desc:
 */
@Service
@Slf4j
public class ImgDataServiceImpl implements ImgDataService {
    @Autowired
    private ImgDataMapper imgDataMapper;
    @Autowired
    private ImgMapper imgMapper;

    @Override
    public Long selectUserMemoryById(Integer id) {
        QueryWrapper<ImgData> lambdaQueryWrapper = new QueryWrapper<>();
        lambdaQueryWrapper.select("select sum(sizes) as sizes");
        lambdaQueryWrapper.eq("user_id",id);
        ImgData imgData = imgDataMapper.selectOne(lambdaQueryWrapper);
        if (imgData == null){
            return 0L;
        }else {
            return Long.parseLong(imgData.getSizes());
        }
    }

    @Override
    public Images selectByImgUid(String imgUid) {
        LambdaQueryWrapper<Images> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Images::getImgUid,imgUid);
        return imgMapper.selectOne(wrapper);
    }
}
