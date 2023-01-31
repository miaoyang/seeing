package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.Keys;
import com.ym.seeing.api.mapper.KeysMapper;
import com.ym.seeing.api.service.IKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 14:25
 * @Desc:
 */
@Service
@Slf4j
public class KeyServiceImpl implements IKeyService {
    @Autowired
    private KeysMapper keysMapper;

    @Override
    public Keys selectKeys(Integer id) {
        LambdaQueryWrapper<Keys> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Keys::getId,id);
        return keysMapper.selectOne(lambdaQueryWrapper);
    }
}
