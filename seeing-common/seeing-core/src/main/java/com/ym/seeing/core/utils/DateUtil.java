package com.ym.seeing.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 10:29
 * @Desc: 时间工具类
 */
@Slf4j
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

    public static String plusDay(int setDay) {
        Date d = new Date();
        String currDate = dateToStr(d);
        log.info("现在的日期是：" + currDate);
        Calendar ca = Calendar.getInstance();
        ca.setTime(d);
        // num为增加的天数，可以改变的
        ca.add(Calendar.DATE, setDay);
        d = ca.getTime();
        String endDate = dateToStr(d);
        log.info("到期的日期：" + endDate);
        return endDate;
    }
}
