package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Keys;
import com.ym.seeing.core.domain.Msg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 14:24
 * @Desc:
 */
public interface IKeyService {
    Keys selectKeys(Integer id);

    List<Keys> getKeys();

    Msg updateKey(Keys keys);

    List<Keys> getStorage();

    List<Keys> getStorageName();
}
