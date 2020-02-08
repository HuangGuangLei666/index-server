package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TGroupTagService;
import com.pl.mapper.TGroupTagMapper;
import com.pl.model.wx.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/19
 */
@Service
public class TGroupTagServiceImpl implements TGroupTagService {

    @Autowired
    private TGroupTagMapper tGroupTagMapper;

    @Override
    public int insertGroupTag(TGroupTag groupTag) {
        return tGroupTagMapper.insertGroupTag(groupTag);
    }

    @Override
    public List<TGroupTagDto> selectByOpenidAndTagname(String openid, String tagName) {
        return tGroupTagMapper.selectByOpenidAndTagname(openid,tagName);
    }

    @Override
    public TGroupTag selectByOpenidAndPhone(String openid, String telephone) {
        return tGroupTagMapper.selectByOpenidAndPhone(openid,telephone);
    }

    @Override
    public int deleteByOpenidAndPhone(String openid, String phone) {
        return tGroupTagMapper.deleteByOpenidAndPhone(openid,phone);
    }

    @Override
    public List<FriendPhonesDto> selectPhoneList(String openid, String tagName, String friendName) {
        return tGroupTagMapper.selectPhoneList(openid,tagName,friendName);
    }

    @Override
    public TGroupTag selectById(Integer id) {
        return tGroupTagMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateById(Integer id, String phone) {
        tGroupTagMapper.updateById(id,phone);
    }

    @Override
    public void insertGroupTags(TGroupTag tGroupTag) {
        tGroupTagMapper.insertGroupTags(tGroupTag);
    }

    @Override
    public void updateByPhonesDto(FriendPhonesDto friendPhonesDto) {
        tGroupTagMapper.updateByPhonesDto(friendPhonesDto);
    }

    @Override
    public void updateByOpenidAndPhones(String openid, String phone, String tagName, String desPhone) {
        tGroupTagMapper.updateByOpenidAndPhones(openid, phone, tagName, desPhone);
    }

    @Override
    public TGroupTag selectByOpenidPhonesTagName(String openid, String phone, String tagName) {
        return tGroupTagMapper.selectByOpenidPhonesTagName(openid, phone, tagName);
    }

    @Override
    public void deletePhonesDto(String openid, String phones, String tagName) {
        tGroupTagMapper.deletePhonesDto(openid, phones, tagName);
    }

    @Override
    public TTagGroup selectByOpenidPhone(String openid, String telephone) {
        return tGroupTagMapper.selectByOpenidPhone(openid,telephone);
    }

    @Override
    public List<TTagGroup> selectGroupClassList() {
        return tGroupTagMapper.selectGroupClassList();
    }

    @Override
    public TTagGroup selectTagNameByTagId(Integer tagId) {
        return tGroupTagMapper.selectTagNameByTagId(tagId);
    }
}
