package com.pl.indexserver.service;

import com.pl.model.wx.*;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/19
 */
public interface TGroupTagService {
    int insertGroupTag(TGroupTag groupTag);

    List<TGroupTagDto> selectByOpenidAndTagname(String openid, String tagName);

    TGroupTag selectByOpenidAndPhone(String openid, String telephone);

    int deleteByOpenidAndPhone(String openid, String phone);

    List<FriendPhonesDto> selectPhoneList(String openid, String tagName, String friendName);

    TGroupTag selectById(Integer id);

    void updateById(Integer id, String phone);

    void insertGroupTags(TGroupTag tGroupTag);

    void updateByPhonesDto(FriendPhonesDto friendPhonesDto);

    void updateByOpenidAndPhones(String openid, String phone, String tagName, String desPhone);

    TGroupTag selectByOpenidPhonesTagName(String openid, String phone, String tagName);

    void deletePhonesDto(String openid, String phones, String tagName);

    TTagGroup selectByOpenidPhone(String openid, String telephone);

    List<TTagGroup> selectGroupClassList();

    TTagGroup selectTagNameByTagId(Integer tagId);
}
