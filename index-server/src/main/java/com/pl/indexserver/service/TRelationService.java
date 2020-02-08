package com.pl.indexserver.service;

import com.pl.model.wx.TRelation;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 14:24
 * @Description
 */
public interface TRelationService {
    int addRelationBinding(TRelation relation);

    TRelation selectByOpenidAndPhone(String openid, String openid1);

    int deleterelationBinding(Integer id);

    TRelation selectByOpenid(String openid, String openid1);

    List<TRelation> selectByRelationOpenid(String openid);

    int updatePassByRelationId(Integer relationId);

    int updateRefuseByRelationId(Integer relationId);
}
