package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TSetService;
import com.pl.mapper.TSetMapper;
import com.pl.model.wx.TSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/26
 */
@Service
public class TSetServiceImpl implements TSetService {

    @Autowired
    private TSetMapper tSetMapper;

    @Override
    public int updateFriendSet(Integer id, Integer userId, String type, Integer voiceId, String phone) {
        return tSetMapper.updateFriendSet(id,userId,type,voiceId,phone);
    }

    @Override
    public int updateGroupSet(Integer id, Integer userId, String type, Integer voiceId, Integer groupId) {
        return tSetMapper.updateGroupSet(id, userId, type, voiceId, groupId);
    }

    @Override
    public TSet selectBusinessVoiceByUserIdAndValue(Integer userId, String phone) {
        return tSetMapper.selectBusinessVoiceByUserIdAndValue(userId,phone);
    }

    @Override
    public TSet selectBusinessVoiceByUserIdOrValue(Integer userId, Integer groupId) {
        return tSetMapper.selectBusinessVoiceByUserIdOrValue(userId,groupId);
    }

    @Override
    public TSet selectTSetByIdUserIdAndType(Integer id, Integer userId, String type) {
        return tSetMapper.selectTSetByIdUserIdAndType(id,userId,type);
    }

    @Override
    public void addTSetList(List<TSet> tSetList) {
        tSetMapper.addTSetList(tSetList);
    }

    @Override
    public List<TSet> selectByUserIdAndValue(Integer userId, String value) {
        return tSetMapper.selectByUserIdAndValue(userId,value);
    }

    @Override
    public List<TSet> selectByUserId(Integer userId) {
        return tSetMapper.selectByUserId(userId);
    }

    @Override
    public List<TSet> selectByOperationId(Integer aLong) {
        return tSetMapper.selectByOperationId(aLong);
    }

    @Override
    public void delOperationRecordByOperationId(Integer operationId) {
        tSetMapper.delOperationRecordByOperationId(operationId);
    }

    @Override
    public List<TSet> selectContentByUserId(Integer userId) {
        return tSetMapper.selectContentByUserId(userId);
    }

    @Override
    public int addContentSet(TSet set) {
        return tSetMapper.addContentSet(set);
    }

    @Override
    public List<TSet> selectprologueByUserId(Integer userId) {
        return tSetMapper.selectprologueByUserId(userId);
    }


}
