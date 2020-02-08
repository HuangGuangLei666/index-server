package com.pl.indexserver.service;

import com.pl.model.wx.TKeyValue; /**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/18 9:50
 * @Description
 */
public interface TKeyValueService {
    int saveCookie(TKeyValue keyValue);

    TKeyValue selectByKey(String openid,String key);
}
