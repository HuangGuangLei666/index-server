package com.pl.mapper;


import com.pl.model.wx.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TGroupTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TGroupTag record);

    int insertSelective(TGroupTag record);

    TGroupTag selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TGroupTag record);

    int updateByPrimaryKey(TGroupTag record);

    int insertGroupTag(TGroupTag groupTag);

    List<TGroupTagDto> selectByOpenidAndTagname(@Param("openid") String openid, @Param("tagName")String tagName);

    TGroupTag selectByOpenidAndPhone(@Param("openid")String openid, @Param("telephone")String telephone);

    int deleteByOpenidAndPhone(@Param("openid")String openid, @Param("phone")String phone);

    List<FriendPhonesDto> selectPhoneList(@Param("openid")String openid,
                                          @Param("tagName")String tagName,
                                          @Param("friendName")String friendName);

    void updateById(@Param("id")Integer id, @Param("phones")String phones);

    void insertGroupTags(TGroupTag tGroupTag);

    void updateByPhonesDto(FriendPhonesDto friendPhonesDto);

    void updateByOpenidAndPhones(@Param("openid")String openid,
                                 @Param("phone")String phone,
                                 @Param("tagName")String tagName,
                                 @Param("desPhone")String desPhone);

    TGroupTag selectByOpenidPhonesTagName(@Param("openid")String openid,
                                          @Param("phone")String phone,
                                          @Param("tagName")String tagName);

    void deletePhonesDto(@Param("openid")String openid,
                         @Param("phones")String phones,
                         @Param("tagName")String tagName);

    TTagGroup selectByOpenidPhone(@Param("openid")String openid, @Param("telephone")String telephone);

    List<TTagGroup> selectGroupClassList();

    TTagGroup selectTagNameByTagId(Integer tagId);
}