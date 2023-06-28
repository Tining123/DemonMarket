package com.tining.demonmarket.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {
    private static final String DATE_FORMAT = "yyyyMMdd";

    private static final String DATE_VIEW_FORMAT = "yyyy-MM-dd";

    /**
     * 将日期字符串转换为Date对象
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseStringToDate(String dateString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.parse(dateString);
    }

    /**
     * 将Date对象转换为日期字符串
     * @param date
     * @return
     */
    public static String formatDateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(date);
    }

    /**
     * 将Date对象转换为方便阅读的日期字符串
     * @param date
     * @return
     */
    public static String formatDateToViewString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_VIEW_FORMAT);
        return formatter.format(date);
    }
}
