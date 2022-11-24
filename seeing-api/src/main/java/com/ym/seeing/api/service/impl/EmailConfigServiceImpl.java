package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.EmailConfig;
import com.ym.seeing.api.mapper.EmailConfigMapper;
import com.ym.seeing.api.service.IEmailConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 20:35
 * @Desc:
 */
@Service
public class EmailConfigServiceImpl implements IEmailConfigService {

    @Autowired
    private EmailConfigMapper emailConfigMapper;

    @Override
    public EmailConfig getEmail() {
        LambdaQueryWrapper<EmailConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        EmailConfig emailConfig = emailConfigMapper.selectOne(lambdaQueryWrapper);
        return emailConfig;
    }

    @Override
    public Integer updateEmail(EmailConfig emailConfig) {
        int insert = emailConfigMapper.insert(emailConfig);
        return insert;
    }
}
