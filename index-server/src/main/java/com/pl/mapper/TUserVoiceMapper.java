package com.pl.mapper;


import com.pl.model.wx.TUserVoice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TUserVoiceMapper {
    int deleteByPrimaryKey(TUserVoice key);

    int insert(TUserVoice record);

    int insertSelective(TUserVoice record);

    TUserVoice selectByPrimaryKey(TUserVoice key);

    int updateByPrimaryKeySelective(TUserVoice record);

    int updateByPrimaryKey(TUserVoice record);

    int insertVoiceToUserCenter(TUserVoice userVoice);

    List<TUserVoice> selectUserBusinessCenter(Integer userId);

    List<TUserVoice> selectVoiceIdByUserId(@Param("userId")Integer userId);

    TUserVoice selectByUserIdAndVoiceId(@Param("userId") Integer userId,
                                        @Param("voiceId") Integer voiceId);
}