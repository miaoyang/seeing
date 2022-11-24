package com.ym.seeing.api.constant;

import lombok.Data;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 20:16
 * @Desc: 返回结果Code 和
 */
public enum ResultConstant {

    InfoEmpty(100000,"传入的信息为空"),

    EmailFormatError(110403,"邮箱格式不正确"),
    UserNameTextExceed(110403,"用户名不得超过20位字符"),
    CloseUserRegister(110403,"本站已暂时关闭用户注册功能"),
    AccountExist(110406,"此用户名已存在"),
    EmailHasRegisted(110407,"此邮箱已被注册"),

    AccountNoActivated(110408,"账号暂未激活"),
    AccountFrozen(110409,"账号被冻结"),
    EmailNotExist(110410,"邮箱不存在"),
    LoginPasswordError(4000,"登录密码错误"),
    LoginError(5000,"登录错误"),


    VerifyCodeExpired(4035,"验证码已失效，请重新弄获取"),
    VerifyCodeEmpty(4036,"验证码为空，请重新获取"),
    VerifyCodeError(4037,"验证码不正确"),

    ServerInternalError(500,"服务器内部错误");


    /**
     * 返回码
     */
    private int code;
    /**
     * 返回信息
     */
    private String msg;


    ResultConstant(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
