package com.pl.indexserver.service;

import com.pl.model.wx.TTag;
import com.pl.model.wx.TUserTag;
import com.pl.model.wx.TUserTagDto;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/19
 */
public interface TUserTagService {

    int insertUserTag(TUserTag tUserTag);

    List<TUserTagDto> selectByOpenidAndTagname(String openid, String tagName);

    TUserTag selectByPhone(String openid, String telephone);

    int deleteByOpenidAndPhone(String openid, String phone);

    List<TTag> selectuserTagList();
}
