package com.ym.seeing.core.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 10:23
 * @Desc: 用于密码的加解密
 */
public class Base64Util {
    /**
     * 解密
     * @param key
     * @return
     */

    public static String decryptBASE64(String key) {
        byte[] b = null;
        try {
            b = (new BASE64Decoder()).decodeBuffer(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(b);
    }

    /**
     * 加密
     *
     * @param key
     * @return
     */
    public static String encryptBASE64(byte[] key) {

        return (new BASE64Encoder()).encodeBuffer(key).replaceAll("\r|\n", "");
    }
}
