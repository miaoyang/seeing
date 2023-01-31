package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.Images;
import com.ym.seeing.api.domain.ImgTemp;
import com.ym.seeing.api.mapper.ImgTempMapper;
import com.ym.seeing.api.service.ImgTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 15:00
 * @Desc:
 */
@Service
public class ImgTempServiceImpl implements ImgTempService {
    @Autowired
    private ImgTempMapper imgTempMapper;

    @Override
    public List<Images> selectDelImgUidList(String dataTime) {
        return null;
    }

    @Override
    public Integer delImgAndExp(String imgUid) {
        LambdaQueryWrapper<ImgTemp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ImgTemp::getImgUid,imgUid);
        return imgTempMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public Integer insertImgExp(ImgTemp imgDataExp) {
        return null;
    }
}
