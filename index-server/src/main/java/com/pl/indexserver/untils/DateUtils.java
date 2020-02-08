package com.pl.indexserver.untils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public final static String DATEFORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public final static String DATEFORMAT_2 = "yyyy-MM-dd";
    public final static String DATEFORMAT_3 = "yyyy-MM-dd HH:mm";
    public final static String DATEFORMAT_4 = "HH:mm:ss";
    public final static String DATEFORMAT_5 = "MM-dd HH:mm";
    public final static String DATEFORMAT_6 = "yyyyMM";
    public final static String DATEFORMAT_7 = "HH:mm";

    /**
     * 将时间字符串按指定格式转换成时间类型
     *
     * @param date   时间字符串
     * @param format 指定转换格式
     * @return 返回转换结果
     * @throws Exception
     */
    public static Date getDate(String date, String format) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(date);
    }

    /**
     * 当前时间格式化输出
     *
     * @return 返回长时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static Date getCurrentDate() {
        return new java.sql.Date(new Date().getTime());
    }

    /**
     * 最近30日
     * @return
     */
    public static Date getBackUp30(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.DATE,-30);
        date = calendar.getTime();
        return date;
    }

    /**
     * 最近7日
     * @return
     */
    public static Date getBackUp7(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.DATE,-7);
        date = calendar.getTime();
        return date;
    }

    /**
     * 昨天
     * @return
     */
    public static Date getBackUpDate(){
        Date date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(calendar.DATE,-1);
        date =calendar.getTime();
        return date;
    }

    /**
     * 将时间按指定格式转换成字符串
     *
     * @param date   时间
     * @param format 指定转换格式
     * @return 返回转换结果
     */
    public static String getStringForDate(Date date, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 将时间字符串转成指定格式的字符串
     *
     * @param date      时间字符串
     * @param oldFormat 传入的时间字符串的格式
     * @param newFormat 需要转换成的字符串格式
     * @return 返回转换结果
     */
    public static String getStringForDateString(String date, String oldFormat, String newFormat) throws Exception {
        if (null == oldFormat) {
            oldFormat = DATEFORMAT_1;
        }
        Date date1 = getDate(date, oldFormat);
        return getStringForDate(date1, newFormat);
    }


}
