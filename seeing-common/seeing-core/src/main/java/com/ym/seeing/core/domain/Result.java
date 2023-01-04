package com.ym.seeing.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 16:49
 * @Desc:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 成功
     */
    private static final int SUCCESS = 200;
    /**
     * 失败
     */
    private static final int FAIL = 500;

    /**
     * 返回标识
     */
    private int code;
    /**
     * 返回数据
     */
    private Object data;
    /**
     *
     */
    private String msg;

    public static Result ok() {
        return result(SUCCESS, null, null);
    }

    public static Result ok(Object data){
        return result(SUCCESS,data,null);
    }

    public static Result ok(Object data, String msg){
        return result(SUCCESS,data,msg);
    }

    public static Result fail(){
        return result(FAIL,null,null);
    }

    public static Result fail(Object data){
        return result(FAIL,data,null);
    }

    public static Result fail(String msg){
        return result(FAIL,null,msg);
    }

    public static Result fail(Object data,String msg){
        return result(FAIL,data,msg);
    }

    public static Result fail(int code,Object data,String msg){
        return result(code,data,msg);
    }

    public static Result result(int code, Object data, String msg) {
        return new ResultBuilder()
                .code(code)
                .data(data)
                .msg(msg)
                .build();
    }

}
