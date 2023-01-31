package com.ym.seeing.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ym.seeing.api.domain.Group;
import com.ym.seeing.api.mapper.GroupMapper;
import com.ym.seeing.api.service.IGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 14:43
 * @Desc:
 */
@Service
@Slf4j
public class GroupServiceImpl implements IGroupService {
    @Autowired
    private GroupMapper groupMapper;

    @Override
    public Integer getCountByUserType(int i) {
        LambdaQueryWrapper<Group> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Group::getUserType,i);
        return groupMapper.selectCount(lambdaQueryWrapper);
    }

    @Override
    public Group selectByUserType(int i) {
        LambdaQueryWrapper<Group> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Group::getUserType,i);
        return groupMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Group selectById(int i) {
        return groupMapper.selectById(i);
    }

    @Override
    public List<Group> groupList(int i) {
        return groupMapper.groupList(i);
    }
}
