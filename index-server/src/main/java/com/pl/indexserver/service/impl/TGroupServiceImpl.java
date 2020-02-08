package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TGoupService;
import com.pl.mapper.TGroupMapper;
import com.pl.model.wx.TGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/19
 */
@Service
public class TGroupServiceImpl implements TGoupService{

    @Autowired
    private TGroupMapper tGroupMapper;

    @Override
    public int insert(TGroup group) {
        return tGroupMapper.insert(group);
    }

    @Override
    public int delGroupByOpenIdAndGroupName(String openId, String groupName) {
        return tGroupMapper.delGroupByOpenIdAndGroupName(openId,groupName);
    }

    @Override
    public int updateGroup(int id,String openId, String groupName, String groupMemberPhones) {
        return tGroupMapper.updateGroup(id,openId,groupName,groupMemberPhones);
    }

    @Override
    public List<TGroup> selectGroupDetailByOpenidAndName(String openId, String groupName) {
        return tGroupMapper.selectGroupDetailByOpenidAndName(openId,groupName);
    }

    @Override
    public List<TGroup> selectGroupNameByOpenId(String openId) {
        return tGroupMapper.selectGroupNameByOpenId(openId);
    }

    @Override
    public TGroup selectGroupById(int groupId) {
        return tGroupMapper.selectByPrimaryKey(groupId);
    }
}
