package com.ym.seeing.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ym.seeing.api.domain.Album;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 17:07
 * @Desc:
 */
@Mapper
public interface AlbumMapper extends BaseMapper<Album> {
    List<Album> selectAlbumURLList(Album album);
}
