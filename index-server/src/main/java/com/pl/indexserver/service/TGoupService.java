package com.pl.indexserver.service;

import com.pl.model.wx.TGroup;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/19
 */
public interface TGoupService {
    int insert(TGroup group);

    int delGroupByOpenIdAndGroupName(String openId, String groupName);

    int updateGroup(int id,String openId, String groupName, String groupMemberPhones);

    List<TGroup> selectGroupDetailByOpenidAndName(String openId, String groupName);

    List<TGroup> selectGroupNameByOpenId(String openId);

    TGroup selectGroupById(int groupId);
}
