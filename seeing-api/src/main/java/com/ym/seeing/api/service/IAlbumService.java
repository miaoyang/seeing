package com.ym.seeing.api.service;

import com.alibaba.fastjson.JSONArray;
import com.ym.seeing.api.domain.Album;
import com.ym.seeing.api.domain.ImageAndAlbum;

import java.util.List;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 17:05
 * @Desc:
 */
public interface IAlbumService {
    JSONArray getAlbumList(JSONArray array);

    Album selectAlbum(Album album);

    Integer addAlbum(Album album);

    Integer deleteAlbum(String albumkey);

    List<Album> selectAlbumURLList(Album album);

    Integer selectAlbumCount(Integer userid);

    Integer updateAlbum(Album album);

    void addAlbumByImgAndAlbum(ImageAndAlbum imgAndAlbum);
}
