package com.ym.seeing.api.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 11:01
 * @Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "code")
public class Code {
    private Integer id;
    private String value;
    private String code;
}
