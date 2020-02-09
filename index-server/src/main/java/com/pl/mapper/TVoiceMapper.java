package com.pl.mapper;


import com.pl.model.wx.TVoice;
import com.pl.model.wx.Voices;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TVoiceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TVoice record);

    int insertSelective(TVoice record);

    TVoice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TVoice record);

    int updateByPrimaryKey(TVoice record);

    List<TVoice> selectByBusinessId(Integer businessId);

    TVoice selectByVoiceId(Integer voiceId);

    int updateBusinessByVoiceId(@Param("voiceId") Integer voiceId,
                                @Param("businessId") Integer businessId);

    TVoice selectByBusinessIdAndVoiceId(@Param("businessId")Integer businessId,
                                        @Param("voiceId")Integer voiceId);

    List<TVoice> selectByBusinessIdAndUserId(@Param("businessId")Integer businessId,
                                             @Param("userId")Integer userId);

    List<TVoice> selectByUserId(Integer userId);

    List<Voices> selectVoiceList();

    List<Voices> selectVoices();

    Voices selectMyVoiceByUserid(Integer userId);
}