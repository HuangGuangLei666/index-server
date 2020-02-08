package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TRelationService;
import com.pl.mapper.TRelationMapper;
import com.pl.model.wx.TRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 14:25
 * @Description
 */
@Service
public class TRelationServiceImpl implements TRelationService {

    @Autowired
    private TRelationMapper tRelationMapper;

    @Override
    public int addRelationBinding(TRelation relation) {
        return tRelationMapper.addRelationBinding(relation);
    }

    @Override
    public TRelation selectByOpenidAndPhone(String openid, String openid1) {
        return tRelationMapper.selectByOpenidAndPhone(openid,openid1);
    }

    @Override
    public int deleterelationBinding(Integer id) {
        return tRelationMapper.deleterelationBinding(id);
    }

    @Override
    public TRelation selectByOpenid(String openid, String openid1) {
        return tRelationMapper.selectByOpenid(openid,openid1);
    }

    @Override
    public List<TRelation> selectByRelationOpenid(String openid) {
        return tRelationMapper.selectByRelationOpenid(openid);
    }

    @Override
    public int updatePassByRelationId(Integer relationId) {
        return tRelationMapper.updatePassByRelationId(relationId);
    }

    @Override
    public int updateRefuseByRelationId(Integer relationId) {
        return tRelationMapper.updateRefuseByRelationId(relationId);
    }
}
