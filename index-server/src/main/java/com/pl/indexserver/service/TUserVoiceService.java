package com.pl.indexserver.service;

import com.pl.model.wx.TUserVoice;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/25
 */
public interface TUserVoiceService {
    int insertVoiceToUserCenter(TUserVoice userVoice);

    List<TUserVoice> selectUserBusinessCenter(Integer userId);

    List<TUserVoice> selectVoiceIdByUserId(Integer userId);

    TUserVoice selectByUserIdAndVoiceId(Integer userId, Integer id);
}
