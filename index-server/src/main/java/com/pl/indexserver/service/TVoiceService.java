package com.pl.indexserver.service;

import com.pl.model.wx.MyVoice;
import com.pl.model.wx.TVoice;
import com.pl.model.wx.Voices;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/24
 */
public interface TVoiceService {
    List<TVoice> selectByBusinessId(Integer businessId);

    TVoice selectByVoiceId(Integer voiceId);

    int updateBusinessByVoiceId(Integer voiceId, Integer businessId);

    TVoice selectByBusinessIdAndVoiceId(Integer businessId, Integer voiceId);

    List<TVoice> selectByBusinessIdAndUserId(Integer businessId, Integer userId);

    List<TVoice> selectByUserId(Integer userId);

    List<Voices> selectVoiceList();

    List<Voices> selectVoices();

    Voices selectMyVoiceByUserid(Integer userId);
}
