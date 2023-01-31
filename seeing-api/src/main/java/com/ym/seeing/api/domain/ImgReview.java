package com.ym.seeing.api.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 20:11
 * @Desc:
 */
@Data
public class ImgReview implements Serializable {
    private Integer id;

    private String appId;

    private String apiKey;

    private String secretKey;

    private Integer using;

    private Integer count;
}
