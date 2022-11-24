package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Config;

public interface IConfigService {

    Config getSourceType();

    Integer setSourceType(Config config);
}
