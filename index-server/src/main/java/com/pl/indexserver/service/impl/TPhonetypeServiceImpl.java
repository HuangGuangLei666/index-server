package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TPhonetypeService;
import com.pl.mapper.TPhonetypeMapper;
import com.pl.model.wx.InterceptSta;
import com.pl.model.wx.TPhonetype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/3 10:43
 * @Description
 */
@Service
public class TPhonetypeServiceImpl implements TPhonetypeService {

    @Autowired
    private TPhonetypeMapper tPhonetypeMapper;

    @Override
    public int addPhoneType(TPhonetype tPhonetype) {
        return tPhonetypeMapper.addPhoneType(tPhonetype);
    }

    @Override
    public List<TPhonetype> selectByOpenidAndType(String openid, String type) {
        return tPhonetypeMapper.selectByOpenidAndType(openid,type);
    }

    @Override
    public TPhonetype selectById(Integer id) {
        return tPhonetypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delectById(Integer id) {
        return tPhonetypeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public TPhonetype selectByOpenidAndPhone(String openid, String callerPhone) {
        return tPhonetypeMapper.selectByOpenidAndPhone(openid,callerPhone);
    }

    @Override
    public void insertInterceptPhone(InterceptSta interceptSta) {
        tPhonetypeMapper.insertInterceptPhone(interceptSta);
    }

    @Override
    public List<InterceptSta> selectIntercepCount(String phonenumber, String format) {
        return tPhonetypeMapper.selectIntercepCount(phonenumber,format);
    }
}
