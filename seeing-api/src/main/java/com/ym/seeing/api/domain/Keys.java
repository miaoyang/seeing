package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/12 19:43
 * @Desc:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "keys")
public class Keys implements Serializable {
    
    private Integer id;
    private String accessKey;
    private String accessSecret;
    private String endpoint;
    private String bucketName;
    private String requestAddress;
    private Integer storageType;
    private String keyName;
}
