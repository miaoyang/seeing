package com.ym.seeing.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Yangmiao
 * @Date: 2022/12/19 17:15
 * @Desc: 常见的全局exception
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;
    /**
     * 错误明细
     */
    private String detailMessage;

    public GlobalException(String message){
        this.message = message;
    }
}
