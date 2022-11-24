package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.EmailConfig;

public interface IEmailConfigService {
    /**
     * 获取邮箱配置信息
     * @return
     */
    EmailConfig getEmail();

    /**
     * 更新邮箱信息
     * @param emailConfig
     * @return
     */
    Integer updateEmail(EmailConfig emailConfig);
}
