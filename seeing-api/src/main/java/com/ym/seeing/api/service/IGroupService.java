package com.ym.seeing.api.service;

import com.ym.seeing.api.domain.Group;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 14:43
 * @Desc:
 */
public interface IGroupService {
    Integer getCountByUserType(int i);

    Group selectByUserType(int i);

    Group selectById(int i);

    List<Group> groupList(int i);
}
