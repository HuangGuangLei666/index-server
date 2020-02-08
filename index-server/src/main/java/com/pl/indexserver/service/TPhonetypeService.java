package com.pl.indexserver.service;

import com.pl.model.wx.InterceptSta;
import com.pl.model.wx.TPhonetype;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/3 10:43
 * @Description
 */
public interface TPhonetypeService {
    int addPhoneType(TPhonetype tPhonetype);

    List<TPhonetype> selectByOpenidAndType(String openid, String type);

    TPhonetype selectById(Integer id);

    int delectById(Integer id);

    TPhonetype selectByOpenidAndPhone(String openid, String callerPhone);

    void insertInterceptPhone(InterceptSta interceptSta);

    List<InterceptSta> selectIntercepCount(String phonenumber, String format);
}
