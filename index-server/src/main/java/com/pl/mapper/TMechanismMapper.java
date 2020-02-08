package com.pl.mapper;


import com.pl.model.wx.TMechanism;
import org.apache.ibatis.annotations.Param;

public interface TMechanismMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TMechanism record);

    int insertSelective(TMechanism record);

    TMechanism selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TMechanism record);

    int updateByPrimaryKey(TMechanism record);

    int insertOrgApply(TMechanism tMechanism);

    TMechanism selectByOrgNum(@Param("code") String code,@Param("orgNum") String orgNum);

    TMechanism selectByOpenId(String openId);
}