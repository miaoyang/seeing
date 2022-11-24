package com.ym.seeing.core.utils;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/24 10:49
 * @Desc:
 */
public class CodeUtil {
    /**
     * 验证码操作
     * @param str
     * @return
     */
    public static String getVerifyCodeOperator(String str){
        int a = Integer.valueOf(str.substring(0,1));
        String yxf = str.substring(1,2);
        int b = Integer.valueOf(str.substring(2,3));
        switch(yxf) {
            case "*":
                return Integer.toString(a * b);
            case "+":
                return Integer.toString(a + b);
            case ",":
            default:
                return Integer.toString(82);
            case "-":
                return Integer.toString(a - b);
        }

    }
}
