package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TKeyValueService;
import com.pl.mapper.TKeyValueMapper;
import com.pl.model.wx.TKeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/18 9:51
 * @Description 有的手机本地缓存没效果，我要把存在手机上 的cookie存到数据库中
 */
@Service
public class TKeyValueServiceImpl implements TKeyValueService {

    @Autowired
    private TKeyValueMapper tKeyValueMapper;

    @Override
    public int saveCookie(TKeyValue keyValue) {
        return tKeyValueMapper.saveCookie(keyValue);
    }

    @Override
    public TKeyValue selectByKey(String openid,String key) {
        return tKeyValueMapper.selectByKey(openid,key);
    }
}
