package com.ym.seeing.api.constant;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/18 20:05
 * @Desc: Redis相关常量
 */
public class RedisConstant {
    /**
     * 注册校验码前缀
     */
    public static final String VERIFY_CODE_FOR_REGISTER = "verifyCodeForRegister_";
    /**
     * 注册header中校验码
     */
    public static final String VERIFY_CODE_FOR_REGISTER_HEADER = "verifyCodeForRegister";
    /**
     * 登录验证码
     */
    public static final String VERIFY_CODE = "verifyCode_";
    /**
     * 登录header验证码
     */
    public static final String VERIFY_CODE_HEADER = "verifyCode";
    /**
     * 取回验证码
     */
    public static final String  VERIFY_CODE_FOR_RETRIEVE= "verifyCodeForRetrieve_";
}
