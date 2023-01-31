package com.ym.seeing.api.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 20:10
 * @Desc:
 */
@Data
public class ImageAndAlbum implements Serializable {
    private String imgName;
    private String albumKey;
    private String notes;
}
