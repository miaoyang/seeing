package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.Config;
import com.ym.seeing.api.mapper.ConfigMapper;
import com.ym.seeing.api.service.IConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 20:46
 * @Desc:
 */
@Service
public class ConfigServiceImpl implements IConfigService {

    @Autowired
    private ConfigMapper configMapper;

    @Override
    public Config getSourceType() {
        LambdaQueryWrapper<Config> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return configMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Integer setSourceType(Config config) {
        return configMapper.insert(config);
    }
}
