package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 17:06
 * @Desc:
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "album")
public class Album {

    @TableField("album_key")
    private String  albumKey;

    @TableField(value = "album_title")
    private String albumTitle;

    @TableField("create_date")
    private String createDate;

    @TableField("password")
    private String password;

    @TableField("user_id")
    private Integer userId;

    @TableField("user_name")
    private String userName;
}
