package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 20:39
 * @Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "config")
public class Config {
    private Integer id;

    @TableField(value = "source_key")
    private Integer sourceKey;

    private Integer email;

    @TableField(value = "web_name")
    private String webName;

    @TableField(value = "c_explain")
    private String cExplain;

    private String video;

    @TableField(value = "back_type")
    private Integer backType;

    private String links;
    private String notice;
    private String baidu;
    private String domain;
    private String background1;
    private String background2;
    private String sett;

    @TableField(value = "web_ms")
    private String webMs;

    @TableField(value = "web_key_words")
    private String webKeyWords;

    @TableField(value = "web_favicons")
    private String webFavicons;

    private Integer theme;

    @TableField(value = "web_sub_title")
    private String webSubTitle;
    private String logo;

    @TableField(value = "about_info")
    private String aboutInfo;
}
