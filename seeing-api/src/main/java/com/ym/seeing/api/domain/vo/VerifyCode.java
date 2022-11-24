package com.ym.seeing.api.domain.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/24 11:22
 * @Desc: 验证码
 */
@Data
@NoArgsConstructor
public class VerifyCode {
    /**
     * UUID生成的codeKey
     */
    private String codeKey;
    /**
     * 验证码图片
     */
    private String codeImg;
}
