package com.ym.seeing.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 11:27
 * @Desc: 用于校验格式
 */
public class VerifyUtil {
    /**
     * 校验邮箱格式是否正确
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^\\w+@[a-zA-Z0-9]{2,10}(?:\\.[a-z]{2,4}){1,3}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
}
