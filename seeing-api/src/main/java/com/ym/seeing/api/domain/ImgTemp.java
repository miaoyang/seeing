package com.ym.seeing.api.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 20:12
 * @Desc:
 */
@Data
public class ImgTemp implements Serializable {
    private Integer id;
    private String imgUid;
    private String delTime;
}
