package com.ym.seeing.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ym.seeing.api.domain.Album;
import com.ym.seeing.api.domain.ImageAndAlbum;
import com.ym.seeing.api.domain.Images;
import com.ym.seeing.api.mapper.AlbumMapper;
import com.ym.seeing.api.mapper.ImageAndAlbumMapper;
import com.ym.seeing.api.mapper.ImgMapper;
import com.ym.seeing.api.service.IAlbumService;
import com.ym.seeing.api.service.ImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 17:08
 * @Desc:
 */
@Service
public class AlbumServiceImpl implements IAlbumService {
    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private ImgService imgService;
    @Autowired
    private ImageAndAlbumMapper imageAndAlbumMapper;

    @Override
    public JSONArray getAlbumList(JSONArray array) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            Images images = new Images();
            images.setImgUid(array.getString(i));
            jsonArray.add(imgService.selectImages(images).get(0));
        }
        return jsonArray;
    }

    @Override
    public Album selectAlbum(Album album) {
        LambdaQueryWrapper<Album> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(album.getAlbumKey())) {
            wrapper.eq(Album::getAlbumKey, album.getAlbumKey());
        }
        if (album.getUserId() != null) {
            wrapper.eq(Album::getUserId, album.getUserId());
        }
        return albumMapper.selectOne(wrapper);
    }

    @Override
    public Integer addAlbum(Album album) {
        return albumMapper.insert(album);
    }

    @Override
    public Integer deleteAlbum(String albumkey) {
        LambdaQueryWrapper<Album> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Album::getAlbumKey, albumkey);
        return albumMapper.delete(wrapper);
    }

    @Override
    public List<Album> selectAlbumURLList(Album album) {
        return albumMapper.selectAlbumURLList(album);
    }

    @Override
    public Integer selectAlbumCount(Integer userid) {
        QueryWrapper<Album> wrapper = new QueryWrapper<>();
        if (userid != null) {
            wrapper.eq("user_id", userid);
        }
        // TODO count值
//        wrapper.select("select count(album_key)");
        return albumMapper.selectCount(wrapper);
    }

    @Override
    public Integer updateAlbum(Album album) {
        LambdaUpdateWrapper<Album> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Album::getAlbumKey, album.getAlbumKey());
        if (StrUtil.isNotEmpty(album.getAlbumTitle())) {
            wrapper.set(Album::getAlbumTitle, album.getAlbumTitle());
        }
        if (StrUtil.isNotEmpty(album.getPassword())) {
            wrapper.set(Album::getPassword, album.getPassword());
        }
        return albumMapper.update(album, wrapper);
    }

    @Override
    public void addAlbumByImgAndAlbum(ImageAndAlbum imgAndAlbum) {
        imageAndAlbumMapper.insert(imgAndAlbum);
    }
}
