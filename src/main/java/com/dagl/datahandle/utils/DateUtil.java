package com.dagl.datahandle.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {
    //日期格式
    public static final Integer[] weeks = new Integer[]{7, 1, 2, 3, 4, 5, 6};
    public static final String format1 = "yyyy-MM-dd HH:mm:ss";
    public static final String format2 = "yyyy-MM-dd HH:mm";
    public static final String format3 = "yyyy-MM-dd";
    public static final String format4 = "yyyyMMdd";
    public static final String format5 = "HHmmssSSSS";
    public static final DateFormat dateFmt1 = new SimpleDateFormat(format1);
    public static final DateFormat dateFmt2 = new SimpleDateFormat(format2);
    public static final DateFormat dateFmt3 = new SimpleDateFormat(format3);
    public static final DateFormat dateFmt4 = new SimpleDateFormat(format4);
    public static final DateFormat dateFmt5 = new SimpleDateFormat(format5);

    /**
     * 日期转换字符串
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        if (date != null) {
            return dateFmt1.format(date);
        }
        return null;
    }

    /**
     * 日期转换字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        if (date != null && format != null && !"".equals(format.trim())) {
            return new SimpleDateFormat(format.trim()).format(date);
        }
        return null;
    }

    /**
     * 字符串转日期
     *
     * @param dateStr
     * @return
     */
    public static Date parseDate(String dateStr) {
        try {
            if (dateStr != null && !"".equals(dateStr.trim())) {
                return dateFmt1.parse(dateStr.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转日期
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date parseDate(String dateStr, String format) {
        try {
            if (dateStr != null && !"".equals(dateStr.trim())) {
                return new SimpleDateFormat(format).parse(dateStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期字符串格式转换
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static String dateFormat(String dateStr, String format) {
        return formatDate(parseDate(dateStr, format), format);
    }

    /**
     * 日期格式转换
     *
     * @param date
     * @param format
     * @return
     */
    public static Date dateParse(Date date, String format) {
        return parseDate(formatDate(date, format), format);
    }

    /**
     * 获取日期的周索引
     *
     * @param date
     * @return
     */
    public static Integer getWeekDateIndex(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 是否本周日期
     *
     * @param date
     * @return
     */
    public static Boolean isCurWeekDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Integer inx1 = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date());
        Integer inx2 = calendar.get(Calendar.WEEK_OF_YEAR);
        if (inx1.intValue() == inx2.intValue()) {
            return true;
        }
        return false;
    }

    /**
     * 是否当周起始日期
     *
     * @param date
     * @return
     */
    public static Boolean isMinWeekDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1] == 1) {
            return true;
        }
        return false;
    }

    /**
     * 获取当周起始日期
     *
     * @param date
     * @return
     */
    public static Date getMinWeekDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = 0;
        if (weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1] == 7) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            day = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.set(Calendar.DAY_OF_YEAR, day - 6);
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            day = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.set(Calendar.DAY_OF_YEAR, day + 1);
        }
        return calendar.getTime();
    }

    /**
     * 获取当周起始日期
     *
     * @param date
     * @param format
     * @return
     */
    public static Date getMinWeekDate(Date date, String format) {
        return dateParse(getMinWeekDate(date), format);
    }

    /**
     * 获取当周截止日期
     *
     * @param date
     * @return
     */
    public static Date getMaxWeekDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1] != 7) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            int day = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.set(Calendar.DAY_OF_YEAR, day + 1);
        }
        return calendar.getTime();
    }

    /**
     * 获取当周截止日期
     *
     * @param date
     * @param format
     * @return
     */
    public static Date getMaxWeekDate(Date date, String format) {
        return dateParse(getMaxWeekDate(date), format);
    }

    /**
     * 是否当月起始日期
     *
     * @param date
     * @return
     */
    public static Boolean isMinMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 获取当月起始日期
     *
     * @param date
     * @return
     */
    public static Date getMinMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 获取当月起始日期
     *
     * @param date
     * @param format
     * @return
     */
    public static Date getMinMonthDate(Date date, String format) {
        return dateParse(getMinMonthDate(date), format);
    }

    /**
     * 获取当月截止日期
     *
     * @param date
     * @return
     */
    public static Date getMaxMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 获取当月截止日期
     *
     * @param date
     * @param format
     * @return
     */
    public static Date getMaxMonthDate(Date date, String format) {
        return dateParse(getMaxMonthDate(date), format);
    }

    /**
     * 获取当季度起始日期
     *
     * @param date
     * @return
     */
    public static Date getMinQuarterDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int quarter = ((calendar.get(Calendar.MONTH) + 1) + 2) / 3;
        calendar.set(Calendar.MONTH, (quarter * 3 - 2) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获取当季度起始日期
     *
     * @param date
     * @return
     */
    public static Date getMinQuarterDate(Date date, String format) {
        return dateParse(getMinQuarterDate(date), format);
    }

    /**
     * 获取当年度起始日期
     *
     * @param date
     * @return
     */
    public static Date getMinYearDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获取当年度起始日期
     *
     * @param date
     * @param format
     * @return
     */
    public static Date getMinYearDate(Date date, String format) {
        return dateParse(getMinYearDate(date), format);
    }

    /**
     * 对一个日期进行日偏移
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date addDayByDate(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, day + offset);
        return calendar.getTime();
    }

    /**
     * 对一个日期进行月偏移
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date addMonthByDate(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month + offset);
        return calendar.getTime();
    }

    /**
     * 对一个日期进行年偏移
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date addYearByDate(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR, year + offset);
        return calendar.getTime();
    }

    /**
     * 对一个日期进行秒偏移
     *
     * @param date
     * @param offset
     * @return
     */
    public static Date addSecondByDate(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int second = calendar.get(Calendar.SECOND);
        calendar.set(Calendar.SECOND, second + offset);
        return calendar.getTime();
    }

    /**
     * 获取日期最早时间
     *
     * @param date
     * @return
     */
    public static Date getMorningDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取日期最晚时间
     *
     * @param date
     * @return
     */
    public static Date getNightDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取日期相差的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static Long getDateDiff(Date date1, Date date2) {
        return ((date1.getTime() - date2.getTime()) / (1000 * 24 * 60 * 60));
    }


    /**
     * 判断是否月末一天
     * @param date
     * @return
     */
    public static boolean isLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }
        return false;
    }

}