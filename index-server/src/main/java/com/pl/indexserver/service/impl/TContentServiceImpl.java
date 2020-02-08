package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TContentService;
import com.pl.mapper.TContentMapper;
import com.pl.model.wx.TContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/24
 */
@Service
public class TContentServiceImpl implements TContentService {

    @Autowired
    private TContentMapper tContentMapper;

    @Override
    public List<TContent> selectContentList(String cpName, String businessDesc) {
        return tContentMapper.selectContentList(cpName,businessDesc);
    }

    @Override
    public TContent selectContentByBusinessId(Integer businessId) {
        return tContentMapper.selectContentByBusinessId(businessId);
    }
}
