package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.UploadConfig;

public interface IUploadConfigService {
    /**
     * 获取配置信息
     * @return
     */
    UploadConfig getUpdateConfig();

    /**
     * 设置上传配置信息
     * @param updateConfig
     * @return
     */
    Integer setUpdateConfig(UploadConfig updateConfig);
}
