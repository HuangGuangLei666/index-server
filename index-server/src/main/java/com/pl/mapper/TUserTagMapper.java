package com.pl.mapper;


import com.pl.model.wx.TTag;
import com.pl.model.wx.TUserTag;
import com.pl.model.wx.TUserTagDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TUserTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TUserTag record);

    int insertSelective(TUserTag record);

    TUserTag selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TUserTag record);

    int updateByPrimaryKey(TUserTag record);

    int insertUserTag(TUserTag tUserTag);

    List<TUserTagDto> selectByOpenidAndTagname(@Param("openid") String openid, @Param("tagName")String tagName);

    TUserTag selectByPhone(@Param("openid")String openid, @Param("telephone")String telephone);

    int deleteByOpenidAndPhone(@Param("openid")String openid, @Param("phone")String phone);

    List<TTag> selectuserTagList();
}