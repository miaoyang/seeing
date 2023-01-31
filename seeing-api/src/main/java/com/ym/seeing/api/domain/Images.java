package com.ym.seeing.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 20:05
 * @Desc:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Images implements Serializable {
    private Integer id;
    private String imgName;
    private String imgUrl;
    private Integer userId;
    private String sizes;
    private String abnormal;
    private Integer source;
    private Integer imgType;
    private String updateTime;
    private String userName;
    private Integer storageType;
    private String startTime;
    private String stopTime;
    private String explains;
    private String md5Key;
    private String notes;
    private String userIdList;
    private String imgUid;
    private String format;
    private String about;
    private Integer great;
    private String violation;
    private String albumTitle;
    //@Length(min = 0, max = 10, message = "画廊密码不能超过10个字符")
    private String password;
    private Integer selectType;
    private Long countNum;
    private Integer monthNum;
    private String yyyy;
    private String[] classifUidList; //类别uid集合
    private String classificationUid; //类别uid集合
    private String idName;
    private Integer isRepeat;
}
