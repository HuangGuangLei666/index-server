package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TUserQaService;
import com.pl.mapper.TUserQaMapper;
import com.pl.model.wx.TUserQa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 17:42
 * @Description
 */
@Service
public class TUserQaServiceImpl implements TUserQaService {

    @Autowired
    private TUserQaMapper tUserQaMapper;

    @Override
    public int addCustomerresp(TUserQa tUserQa) {
        return tUserQaMapper.addCustomerresp(tUserQa);
    }

    @Override
    public TUserQa selectAnswerId(String knowledgeId, String openid) {
        return tUserQaMapper.selectAnswerId(knowledgeId,openid);
    }
}
