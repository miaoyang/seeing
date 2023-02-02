package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ym.seeing.api.domain.Keys;
import com.ym.seeing.api.mapper.KeysMapper;
import com.ym.seeing.api.service.IKeyService;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.datasource.upload.GetSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Autowired
    private GetSource getSource;

    @Override
    public Keys selectKeys(Integer id) {
        LambdaQueryWrapper<Keys> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Keys::getId, id);
        return keysMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public List<Keys> getKeys() {
        LambdaQueryWrapper<Keys> wrapper = new LambdaQueryWrapper<>();
        return keysMapper.selectList(wrapper);
    }

    @Override
    public Msg updateKey(Keys keys) {
        Msg msg = new Msg();
        com.ym.seeing.datasource.domain.Keys tempKeys = new com.ym.seeing.datasource.domain.Keys();
        BeanUtils.copyProperties(keys, tempKeys);
        Integer ret = getSource.getInitType(tempKeys);

        LambdaUpdateWrapper<Keys> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Keys::getStorageType, keys.getStorageType());

        if (ret > 0) {
            keysMapper.update(keys, wrapper);
            msg.setInfo("保存成功");
        } else {
            if (keys.getStorageType() == 5) {
                keysMapper.update(keys, wrapper);
                msg.setInfo("保存成功");
            } else {
                msg.setCode("4002");
                msg.setInfo("对象存储初始化失败,请检查参数是否正确");
            }
        }
        return msg;
    }

    @Override
    public List<Keys> getStorage() {
        return keysMapper.getStorage();
    }

    @Override
    public List<Keys> getStorageName() {
        LambdaQueryWrapper<Keys> wrapper = new LambdaQueryWrapper<>();
        return keysMapper.selectList(wrapper);
    }
}
