package com.pl.indexserver.service;

import com.pl.model.wx.TBook;
import com.pl.model.wx.TUserinfo;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/18
 */
public interface TBookService {

    int insert(TBook book);

    int delFriendByOpenIdAndPhone(String openId, String phone);

    int updateFriendBookByPhone(String phone, String openId, String friendName);

    List<TBook> qryFriendDetail(String openId, String phone);

    TBook qryFriendByOpenidAndPhone(String openId, String phone);

    List<TBook> selectFriendByOpenId(String openid);

    TBook selectFriendByPhone(String phone);

    TBook selectByPhoneAndOpenid(String value, String openid);

    int insertNotFriendName(TBook tBook);

    TBook selectFriendByGroupTagId(Integer id);

    int updateByOpenidAndPhone(String openid, String phones, String phone, String friendName);

    int delByOpenIdAndFriendName(String openid, String friendName);

    void updateByfriendNameAndPhone(String openid,String friendName, String phone, String desName, String desPhone);

    TBook selectByOpenidPhoneFriendName(String openid, String phone, String desName);

    void deletePhonesDto(String openid, String phone, String friendName);

    TBook selectByOpenidFriendName(String openid, String desName);

    void deletePhonesDto1(String openid, String friendName);

    void delByOpenIdFriendNamePh(String openid, String name, String ph);
}
