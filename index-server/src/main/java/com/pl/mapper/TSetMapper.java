package com.pl.mapper;


import com.pl.model.wx.TSet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TSetMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TSet record);

    int insertSelective(TSet record);

    TSet selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TSet record);

    int updateByPrimaryKey(TSet record);

    int updateFriendSet(@Param("id") Integer id,
                        @Param("userId") Integer userId,
                        @Param("type") String type,
                        @Param("voiceId") Integer voiceId,
                        @Param("phone") String phone);

    int updateGroupSet(@Param("id") Integer id,
                       @Param("userId") Integer userId,
                       @Param("type") String type,
                       @Param("voiceId") Integer voiceId,
                       @Param("groupId") Integer groupId);

    TSet selectBusinessVoiceByUserIdAndValue(@Param("userId") Integer userId,
                                             @Param("phone") String phone);

    TSet selectBusinessVoiceByUserIdOrValue(@Param("userId") Integer userId,
                                            @Param("groupId") Integer groupId);

    TSet selectTSetByIdUserIdAndType(@Param("id") Integer id,
                                     @Param("userId") Integer userId,
                                     @Param("type") String type);

    void addTSetList(@Param("tSetList") List<TSet> tSetList);

    List<TSet> selectByUserIdAndValue(@Param("userId")Integer userId,
                                      @Param("value")String value);

    List<TSet> selectByUserId(Integer userId);

    List<TSet> selectByOperationId(Integer aLong);

    void delOperationRecordByOperationId(Integer operationId);

    List<TSet> selectContentByUserId(Integer userId);

    int addContentSet(TSet set);

    List<TSet> selectprologueByUserId(Integer userId);

    int addMyContentSet(TSet set);

    TSet selectCurrentSetContentByUserId(Integer userId);

}