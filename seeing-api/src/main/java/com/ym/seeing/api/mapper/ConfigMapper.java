package com.ym.seeing.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ym.seeing.api.domain.Config;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
    
}
