package com.pl.indexserver.untils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class StampToDates {
    /*
     * 将时间戳转换为时间
     */
    public static long stampToDate(String bengintime, String endtime) {
        long lt = new Long(bengintime);
        Date date = new Date(lt);
        long lt2 = new Long(endtime);
        Date date2 = new Date(lt2);
        long mis = date2.getTime() - date.getTime();
        return mis / 1000;
    }

    public static long stampToDateReduce(String bengintime, String endtime) {
        long mis = 0;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            long lt = simpleDateFormat.parse(bengintime).getTime();

            long lt2 = simpleDateFormat.parse(endtime).getTime();

            mis = lt2 - lt;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mis / 1000;
    }


    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (s != null) {
            long lt = new Long(s);
            Date date = new Date(lt);
            res = simpleDateFormat.format(date);
            return res;
        } else {
            return simpleDateFormat.format(new Date());
        }

    }

    /*
   * 将时间转换为时间戳
   */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        Long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /**
     * Date类型转为指定格式的String类型
     *
     * @param source
     * @param pattern
     * @return
     */
    public static String dateToString(Date source, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(source);
    }

    /**
     * 字符串转换为对应日期
     *
     * @param source
     * @param pattern
     * @return
     */
    public static Date stringToDate(String source, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = simpleDateFormat.parse(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     * 获取 xx-xx 之间的日期
     * @param beginDate
     * @param endDate
     * @param time
     * @return
     */
    public static List<String> getDatesBetweenTwoDate(Date beginDate, Date endDate ,String time){
        List<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> lDate = new ArrayList<>();
        lDate.add(sdf.format(beginDate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(sdf.format(cal.getTime()));
            } else {
                bContinue = false;
            }
        }
        if(!sdf.format(beginDate.getTime()).equals(sdf.format(endDate.getTime()))){
            lDate.add(sdf.format(endDate));
        }
        String [] times = time.split("\\|");
        for (String ss:lDate) {
            for (String str:times) {
                String [] mmss = str.split("-");
                for (int j = 0; j < 1 ; j++){
                    String ll = String.format("%s %s:00|%s %s:00",ss,mmss[0],ss,mmss[1]);
                    result.add(ll);
                }
            }
        }
        return result;
    }

}
