package com.ym.seeing.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ym.seeing.api.domain.Images;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 20:16
 * @Desc:
 */
@Mapper
public interface ImgMapper extends BaseMapper<Images> {
    List<Images> selectImages(Images images);

    Long getSourceMemory(Integer keyId);

    List<Images> getRecentlyUploaded(Integer id);

    List<String> getyyyy(Integer id);

    List<Images> countByUpdateTime(Images images);
}
