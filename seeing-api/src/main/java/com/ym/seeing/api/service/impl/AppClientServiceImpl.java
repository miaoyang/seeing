package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ym.seeing.api.domain.AppClient;
import com.ym.seeing.api.mapper.AppClientMapper;
import com.ym.seeing.api.service.IAppClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/24 14:40
 * @Desc:
 */
@Service
public class AppClientServiceImpl implements IAppClientService {

    @Autowired
    private AppClientMapper appClientMapper;

    @Override
    public AppClient getAppClientData(String id) {
        LambdaQueryWrapper<AppClient> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AppClient::getId,id);
        return appClientMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Integer editAppClientData(AppClient appClient) {
        LambdaUpdateWrapper<AppClient> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.setEntity(appClient);
        lambdaUpdateWrapper.eq(AppClient::getId,appClient.getId());
        return appClientMapper.update(appClient,lambdaUpdateWrapper);
    }
}
