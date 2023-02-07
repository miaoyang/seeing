package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 20:11
 * @Desc:
 */
@Data
@TableName("img_review")
public class ImgReview implements Serializable {
    private Integer id;

    @TableField("app_id")
    private String appId;

    @TableField("api_key")
    private String apiKey;

    @TableField("secret_key")
    private String secretKey;

    @TableField("using")
    private Integer using;

    private Integer count;
}
