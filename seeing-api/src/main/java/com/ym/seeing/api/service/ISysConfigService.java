package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.SysConfig;

public interface ISysConfigService {
    SysConfig getState();

    Integer setState(SysConfig sysConfig);

}
