package com.pl.mapper;


import com.pl.model.wx.TKeyValue;
import org.apache.ibatis.annotations.Param;

public interface TKeyValueMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TKeyValue record);

    int insertSelective(TKeyValue record);

    TKeyValue selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TKeyValue record);

    int updateByPrimaryKey(TKeyValue record);

    int saveCookie(TKeyValue keyValue);

    TKeyValue selectByKey(@Param("openid")String openid, @Param("key")String key);
}