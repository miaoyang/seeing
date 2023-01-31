package com.ym.seeing.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:50
 * @Desc:
 */
@Getter
public class StorageSourceInitException extends RuntimeException{
    private String msg;

    public StorageSourceInitException(String msg){
        super(msg);
        this.msg = msg;
    }
    public StorageSourceInitException(String msg,Throwable cause){
        super(msg,cause);
        this.msg = msg;
    }
}
