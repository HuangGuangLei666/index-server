package com.pl.indexserver.untils;

import com.alibaba.fastjson.JSON;
import com.pl.model.TmUser;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetUid {

    public static TmUser getUID(HttpServletRequest request,RedisClient redisClient){
        try {
            String header = request.getHeader("token");
            if( null == header) {
                header = request.getParameter("token");
            }
            return JSON.parseObject(redisClient.get(header),TmUser.class);
        }catch (Exception e){
            return null;
        }
    }

    public static boolean isLetterDigitOrChinese(String str) {
        boolean isfg;
        String regex=".*[a-zA-Z]+.*";
        Matcher m=Pattern.compile(regex).matcher(str);
        isfg = m.matches();
        if(isfg){
            return isfg;
        }
        //循环遍历字符串
        for(int i=0 ; i < str.length() ; i++){
            //用char包装类中的判断数字的方法判断每一个字符
            if(Character.isDigit(str.charAt(i))){
                isfg = true;
               return isfg;
            }
        }
        return isfg;
    }
}
