package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TCallChannelService;
import com.pl.mapper.TCallChannelMapper;
import com.pl.model.TCallChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TCallChannelServiceImpl implements TCallChannelService {

    @Autowired
    private TCallChannelMapper tCallChannelMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return tCallChannelMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(TCallChannel record) {
        return tCallChannelMapper.insert(record);
    }

    @Override
    public int insertSelective(TCallChannel record) {
        return tCallChannelMapper.insertSelective(record);
    }

    @Override
    public TCallChannel selectByPrimaryKey(Long id) {
        return tCallChannelMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(TCallChannel record) {
        return tCallChannelMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(TCallChannel record) {
        return tCallChannelMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<TCallChannel> getCallChannelNameByCompany_id(Long company_id) {
        return tCallChannelMapper.getCallChannelNameByCompany_id(company_id);
    }
}
