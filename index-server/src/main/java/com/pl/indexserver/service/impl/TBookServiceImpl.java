package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TBookService;
import com.pl.mapper.TBookMapper;
import com.pl.model.wx.TBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/18
 */
@Service
public class TBookServiceImpl implements TBookService {

    @Autowired
    private TBookMapper tBookMapper;

    @Override
    public int insert(TBook book) {
        return tBookMapper.insert(book);
    }

    @Override
    public int delFriendByOpenIdAndPhone(String openId, String phone) {
        return tBookMapper.delFriendByOpenIdAndPhone(openId,phone);
    }

    @Override
    public int updateFriendBookByPhone(String phone, String openId, String friendName) {
        return tBookMapper.updateFriendBookByPhone(phone,openId,friendName);
    }

    @Override
    public List<TBook> qryFriendDetail(String openId, String phone) {
        return tBookMapper.qryFriendDetail(openId,phone);
    }

    @Override
    public TBook qryFriendByOpenidAndPhone(String openId, String phone) {
        return tBookMapper.qryFriendByOpenidAndPhone(openId,phone);
    }

    @Override
    public List<TBook> selectFriendByOpenId(String openid) {
        return tBookMapper.selectFriendByOpenId(openid);
    }

    @Override
    public TBook selectFriendByPhone(String phone) {
        return tBookMapper.selectFriendByPhone(phone);
    }

    @Override
    public TBook selectByPhoneAndOpenid(String value, String openid) {
        return tBookMapper.selectByPhoneAndOpenid(value,openid);
    }

    @Override
    public int insertNotFriendName(TBook tBook) {
        return tBookMapper.insertNotFriendName(tBook);
    }

    @Override
    public TBook selectFriendByGroupTagId(Integer id) {
        return tBookMapper.selectFriendByGroupTagId(id);
    }

    @Override
    public int updateByOpenidAndPhone(String openid, String phones, String phone, String friendName) {
        return tBookMapper.updateByOpenidAndPhone(openid,phones,phone,friendName);
    }

    @Override
    public int delByOpenIdAndFriendName(String openid, String friendName) {
        return tBookMapper.delByOpenIdAndFriendName(openid,friendName);
    }

    @Override
    public void updateByfriendNameAndPhone(String openid,String friendName, String phone, String desName, String desPhone) {
        tBookMapper.updateByfriendNameAndPhone(openid,friendName, phone, desName, desPhone);
    }

    @Override
    public TBook selectByOpenidPhoneFriendName(String openid, String phone, String desName) {
        return tBookMapper.selectByOpenidPhoneFriendName(openid, phone, desName);
    }

    @Override
    public void deletePhonesDto(String openid, String phone, String friendName) {
        tBookMapper.deletePhonesDto(openid, phone, friendName);
    }

    @Override
    public TBook selectByOpenidFriendName(String openid, String desName) {
        return tBookMapper.selectByOpenidFriendName(openid, desName);
    }

    @Override
    public void deletePhonesDto1(String openid, String friendName) {
        tBookMapper.deletePhonesDto1(openid, friendName);
    }

    @Override
    public void delByOpenIdFriendNamePh(String openid, String name, String ph) {
        tBookMapper.delByOpenIdFriendNamePh(openid, name,ph);
    }

}
