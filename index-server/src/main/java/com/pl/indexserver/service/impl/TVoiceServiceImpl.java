package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TVoiceService;
import com.pl.mapper.TVoiceMapper;
import com.pl.model.wx.MyVoice;
import com.pl.model.wx.TVoice;
import com.pl.model.wx.Voices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/24
 */
@Service
public class TVoiceServiceImpl implements TVoiceService {

    @Autowired
    private TVoiceMapper tVoiceMapper;

    @Override
    public List<TVoice> selectByBusinessId(Integer businessId) {
        return tVoiceMapper.selectByBusinessId(businessId);
    }

    @Override
    public TVoice selectByVoiceId(Integer voiceId) {
        return tVoiceMapper.selectByVoiceId(voiceId);
    }

    @Override
    public int updateBusinessByVoiceId(Integer voiceId, Integer businessId) {
        return tVoiceMapper.updateBusinessByVoiceId(voiceId,businessId);
    }

    @Override
    public TVoice selectByBusinessIdAndVoiceId(Integer businessId, Integer voiceId) {
        return tVoiceMapper.selectByBusinessIdAndVoiceId(businessId,voiceId);
    }

    @Override
    public List<TVoice> selectByBusinessIdAndUserId(Integer businessId, Integer userId) {
        return tVoiceMapper.selectByBusinessIdAndUserId(businessId,userId);
    }

    @Override
    public List<TVoice> selectByUserId(Integer userId) {
        return tVoiceMapper.selectByUserId(userId);
    }

    @Override
    public List<Voices> selectVoiceList() {
        return tVoiceMapper.selectVoiceList();
    }

    @Override
    public List<Voices> selectVoices() {
        return tVoiceMapper.selectVoices();
    }

    @Override
    public Voices selectMyVoiceByUserid(Integer userId) {
        Voices voice = tVoiceMapper.selectMyVoiceByUserid(userId);
        if (!StringUtils.isEmpty(voice)){
            return voice;
        }
        return new Voices("您还没有选择声音，请先勾选一个声音");
    }
}
