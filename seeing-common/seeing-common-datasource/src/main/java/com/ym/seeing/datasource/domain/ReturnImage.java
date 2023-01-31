package com.ym.seeing.datasource.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 15:11
 * @Desc:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnImage {
    private String uid;
    private String code;
    private String imgurl;
    private String imgname;
    private Long imgSize;
}
