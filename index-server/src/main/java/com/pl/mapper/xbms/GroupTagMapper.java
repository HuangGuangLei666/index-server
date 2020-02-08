package com.pl.mapper.xbms;

import com.pl.mapper.TGroupTagMapper;
import com.pl.model.wx.FriendPhonesDto;
import com.pl.model.wx.TTagGroup;
import com.pl.model.xbms.GroupTag;
import com.pl.model.xbms.GroupTagDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2020/1/16 14:52
 * @Description
 */
public interface GroupTagMapper {

    GroupTag selectGtagByOpenidAndPhone(@Param("userId") Integer userId, @Param("telephone") String telephone);

    TTagGroup selectByUserIdAndPhone(@Param("userId")Integer userId, @Param("telephone")String telephone);

    int insertGroupTag(GroupTag groupTag);

    List<GroupTagDto> selectByUseridAndTagname(@Param("userId")Integer userId, @Param("tagName")String tagName);

    List<FriendPhonesDto> selectPhoneList(@Param("userId")Integer userId,
                                          @Param("tagName")String tagName,
                                          @Param("friendName")String friendName);

    TTagGroup selectTagNameByTagId(Integer tagId);

    void deletePhonesDto(@Param("userId")Integer userId,
                         @Param("phone")String s,
                         @Param("tagName")String name);

    int deleteByOpenidAndPhone(@Param("userId")Integer userId, @Param("phone")String phone);
}
