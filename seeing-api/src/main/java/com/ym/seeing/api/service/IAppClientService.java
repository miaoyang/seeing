package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.AppClient;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/24 14:40
 * @Desc:
 */
public interface IAppClientService {
    /**
     * 获取app客户端信息
     * @param id
     * @return
     */
    AppClient getAppClientData(String id);

    /**
     * 修改app客户端信息
     * @param appClient
     * @return
     */
    Integer editAppClientData(AppClient appClient);
}
