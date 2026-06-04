package com.gym.common.utils;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String YYYY_MM_DD = "yyyy-MM-dd";
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String dateTimeNow() {
        return parseDateToStr(YYYY_MM_DD_HH_MM_SS, new Date());
    }

    public static String parseDateToStr(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }
}