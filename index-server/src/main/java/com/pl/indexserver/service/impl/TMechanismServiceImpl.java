package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TMechanismService;
import com.pl.mapper.TMechanismMapper;
import com.pl.model.wx.TMechanism;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author HuangGuangLei
 * @Date 2019/11/8
 */
@Service
public class TMechanismServiceImpl implements TMechanismService {

    @Autowired
    private TMechanismMapper tMechanismMapper;

    @Override
    public int insertOrgApply(TMechanism tMechanism) {
        return tMechanismMapper.insertOrgApply(tMechanism);
    }

    @Override
    public TMechanism selectByOrgNum(String code, String orgNum) {
        return tMechanismMapper.selectByOrgNum(code,orgNum);
    }

    @Override
    public TMechanism selectByOpenId(String openId) {
        return tMechanismMapper.selectByOpenId(openId);
    }
}
