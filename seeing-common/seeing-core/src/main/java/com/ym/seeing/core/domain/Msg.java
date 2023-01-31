package com.ym.seeing.core.domain;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/6 10:46
 * @Desc: 前后交互json
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Msg implements Serializable {

    private static final long serialVersionUID = 5196249482551119279L;
    /**
     * 返回码
     */
    private String code ="200";

    /**
     * 提示信息
     */
    private String info="操作成功";

    /**
     * 返回数据
     */
    private Object data;

    /**
     * 异常
     */
    private String exceptions="服务器内部错误";
    /**
     * 默认提示info
     */
    private static final String INFO = "操作成功";
    /**
     * 成功
     */
    private static final String SUCCESS = "200";
    /**
     * 失败
     */
    private static final String  FAIL = "500";

    /**
     * 默认错误
     */
    private static final String EXCEPTION = "服务器内部错误";


    public static Msg ok() {
        return msg(SUCCESS, null, null,null);
    }

    public static Msg ok(Object data){
        return msg(SUCCESS,null,data,null);
    }

    public static Msg ok(Object data, String msg){
        return msg(SUCCESS,msg,data,null);
    }

    public static Msg fail(){
        return msg(FAIL,null,null,null);
    }

    public static Msg fail(Object data){
        return msg(FAIL,null,data,null);
    }

    public static Msg fail(String msg){
        return msg(FAIL,msg,null,null);
    }

    public static Msg fail(String code,String msg){
        return msg(code,msg,null,null);
    }

    public static Msg fail(Object data,String msg){
        return msg(FAIL,msg,data,null);
    }

    public static Msg fail(String code,Object data,String msg){
        return msg(code,msg,data,null);
    }

    public static Msg msg(String code,String info,Object data,String exceptions){
        return new MsgBuilder()
                .code(StrUtil.isEmpty(code)?SUCCESS:code)
                .info(StrUtil.isEmpty(info)?INFO:info)
                .data(data)
                .exceptions(StrUtil.isEmpty(exceptions)?EXCEPTION:exceptions)
                .build();
    }
}
