package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 10:57
 * @Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "img_data")
public class ImgData {

    private Integer id;

    @TableField(value = "img_name")
    private String imgName;

    @TableField(value = "img_url")
    private String imgUrl;

    @TableField(value = "user_id")
    private Integer userId;

    @TableField(value = "update_time")
    private String updateTime;

    private String sizes;

    private String abnormal;
    private Integer source;

    @TableField(value = "img_type")
    private Integer imgType;

    private String explains;

    @TableField(value = "md5_key")
    private String md5Key;

    @TableField(value = "img_uid")
    private String imgUid;

    private String format;
    private String about;

    private String violation;

    @TableField(value = "id_name")
    private String idName;




//    private String username;
//    private Integer storageType;
//    private String starttime;
//    private String stoptime;
//
//
//    private String notes;
//    private String useridlist;
//
//
//    private Integer great;
//
//    private String albumtitle;
//    //@Length(min = 0, max = 10, message = "画廊密码不能超过10个字符")
//    private String password;
//    private Integer selecttype;
//    private Long countNum;
//    private Integer monthNum;
//    private String yyyy;
//    private String[] classifuidlist; //类别uid集合
//    private String classificationuid; //类别uid集合



}
