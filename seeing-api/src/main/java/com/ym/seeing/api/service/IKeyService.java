package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Keys;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 14:24
 * @Desc:
 */
public interface IKeyService {
    Keys selectKeys(Integer id);
}
