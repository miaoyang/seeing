package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.SysConfig;
import com.ym.seeing.api.mapper.SysConfigMapper;
import com.ym.seeing.api.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 20:34
 * @Desc:
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public SysConfig getState() {
        LambdaQueryWrapper<SysConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return sysConfigMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Integer setState(SysConfig sysConfig) {
        return sysConfigMapper.insert(sysConfig);
    }
}
