package com.ym.seeing.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 10:29
 * @Desc: 时间工具类
 */
public class DateUtil {

    /**
     * 日期转换为时间字符串格式
     * @param date
     * @return
     */
    public static String dateToStr(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
