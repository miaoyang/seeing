package com.ym.seeing.core.utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Yangmiao
 * @Date: 2023/1/13 16:46
 * @Desc:
 */
public class TextUtil {
    //获取8位短uuid
    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z" };

    public static String getShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    public static String get32UUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    public static String getChinaMonth(int v) {
        String ch = "";
        switch (v) {
            case 1:
                ch = "一月";
                break; // 可选
            case 2:
                ch = "二月";
                break; // 可选
            case 3:
                ch = "三月";
                break; // 可选
            case 4:
                ch = "四月";
                break; // 可选
            case 5:
                ch = "五月";
                break; // 可选
            case 6:
                ch = "六月";
                break; // 可选
            case 7:
                ch = "七月";
                break; // 可选
            case 8:
                ch = "八月";
                break; // 可选
            case 9:
                ch = "九月";
                break; // 可选
            case 10:
                ch = "十月";
                break; // 可选
            case 11:
                ch = "十一月";
                break; // 可选
            case 12:
                ch = "十二月";
                break; // 可选
            default:
                ch = ""; // 可选
                // 语句
        }
        return ch;
    }

    /**
     * 验证邮箱
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
