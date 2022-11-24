package com.ym.seeing.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ym.seeing.core.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 19:40
 * @Desc:
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
