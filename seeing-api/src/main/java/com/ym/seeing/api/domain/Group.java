package com.ym.seeing.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 19:58
 * @Desc:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    private Integer id;
    private String groupName;
    private Integer keyId;
    private Integer userType;
    private Integer compress;
    private Integer storageType;
    private String keyName;
}
