package com.pl.mapper;


import com.pl.model.wx.TRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TRelationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TRelation record);

    int insertSelective(TRelation record);

    TRelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TRelation record);

    int updateByPrimaryKey(TRelation record);

    int addRelationBinding(TRelation relation);

    TRelation selectByOpenidAndPhone(@Param("openid") String openid, @Param("openid1")String openid1);

    int deleterelationBinding(Integer id);

    TRelation selectByOpenid(@Param("openid")String openid, @Param("openid1")String openid1);

    List<TRelation> selectByRelationOpenid(String openid);

    int updatePassByRelationId(Integer relationId);

    int updateRefuseByRelationId(Integer relationId);
}