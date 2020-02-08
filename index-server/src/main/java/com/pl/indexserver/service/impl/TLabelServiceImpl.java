package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TLabelService;
import com.pl.mapper.TLabelMapper;
import com.pl.model.wx.TLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/28
 */
@Service
public class TLabelServiceImpl implements TLabelService {

    @Autowired
    private TLabelMapper tLabelMapper;

    @Override
    public TLabel selectLabelById(Integer sysLabelId) {
        return tLabelMapper.selectByPrimaryKey(sysLabelId);
    }

    @Override
    public List<TLabel> selectLabelListByFathId(Integer fathId) {
        return tLabelMapper.selectLabelListByFathId(fathId);
    }

    @Override
    public List<TLabel> selectLabelByLevel(String level) {
        return tLabelMapper.selectLabelByLevel(level);
    }

    @Override
    public List<TLabel> selectAllData() {
        return tLabelMapper.selectAllData();
    }
}
