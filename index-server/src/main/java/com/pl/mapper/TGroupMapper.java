package com.pl.mapper;
import com.pl.model.wx.TGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TGroup group);

    int insertSelective(TGroup record);

    TGroup selectByPrimaryKey(Integer groupId);

    int updateByPrimaryKeySelective(TGroup record);

    int updateByPrimaryKey(TGroup record);

    int delGroupByOpenIdAndGroupName(@Param("openId") String openId, @Param("groupName")String groupName);

    int updateGroup(@Param("id")Integer id,@Param("openId")String openId, @Param("groupName")String groupName,
                    @Param("groupMemberPhones")String groupMemberPhones);

    List<TGroup> selectGroupDetailByOpenidAndName(@Param("openId")String openId, @Param("groupName")String groupName);

    List<TGroup> selectGroupNameByOpenId(String openId);
}