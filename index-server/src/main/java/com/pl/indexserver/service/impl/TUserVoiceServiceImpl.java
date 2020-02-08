package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TUserVoiceService;
import com.pl.mapper.TUserVoiceMapper;
import com.pl.model.wx.TUserVoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/25
 */
@Service
public class TUserVoiceServiceImpl implements TUserVoiceService {

    @Autowired
    private TUserVoiceMapper tUserVoiceMapper;

    @Override
    public int insertVoiceToUserCenter(TUserVoice userVoice) {
        return tUserVoiceMapper.insertVoiceToUserCenter(userVoice);
    }

    @Override
    public List<TUserVoice> selectUserBusinessCenter(Integer userId) {
        return tUserVoiceMapper.selectUserBusinessCenter(userId);
    }

    @Override
    public List<TUserVoice> selectVoiceIdByUserId(Integer userId) {
        return tUserVoiceMapper.selectVoiceIdByUserId(userId);
    }

    @Override
    public TUserVoice selectByUserIdAndVoiceId(Integer userId, Integer voiceId) {
        return tUserVoiceMapper.selectByUserIdAndVoiceId(userId,voiceId);
    }
}
