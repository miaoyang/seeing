package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.UploadConfig;
import com.ym.seeing.api.mapper.UploadConfigMapper;
import com.ym.seeing.api.service.IUploadConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 20:34
 * @Desc: 上传配置信息更新
 */
@Service
@Slf4j
public class UploadConfigServiceImpl implements IUploadConfigService {

    @Autowired
    private UploadConfigMapper uploadConfigMapper;

    @Override
    public UploadConfig getUpdateConfig() {
        LambdaQueryWrapper<UploadConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UploadConfig::getId,1);
        UploadConfig uploadConfig = uploadConfigMapper.selectOne(lambdaQueryWrapper);
        return uploadConfig;
    }

    @Override
    public Integer setUpdateConfig(UploadConfig updateConfig) {
        int insert = uploadConfigMapper.insert(updateConfig);
        return insert;
    }
}
