package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TUserTagService;
import com.pl.mapper.TUserTagMapper;
import com.pl.model.wx.TTag;
import com.pl.model.wx.TUserTag;
import com.pl.model.wx.TUserTagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/19
 */
@Service
public class TUserTagServiceImpl implements TUserTagService {

    @Autowired
    private TUserTagMapper tUserTagMapper;

    @Override
    public int insertUserTag(TUserTag tUserTag) {
        return tUserTagMapper.insertUserTag(tUserTag);
    }

    @Override
    public List<TUserTagDto> selectByOpenidAndTagname(String openid, String tagName) {
        return tUserTagMapper.selectByOpenidAndTagname(openid,tagName);
    }

    @Override
    public TUserTag selectByPhone(String openid, String telephone) {
        return tUserTagMapper.selectByPhone(openid,telephone);
    }

    @Override
    public int deleteByOpenidAndPhone(String openid, String phone) {
        return tUserTagMapper.deleteByOpenidAndPhone(openid,phone);
    }

    @Override
    public List<TTag> selectuserTagList() {
        return tUserTagMapper.selectuserTagList();
    }
}
