package com.ym.seeing.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ym.seeing.api.domain.Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/16 14:51
 * @Desc:
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group> {
    List<Group> groupList(int userType);
}
