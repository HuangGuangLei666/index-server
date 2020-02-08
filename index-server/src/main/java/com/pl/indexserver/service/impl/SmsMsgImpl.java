package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.SmsMsgService;
import com.pl.mapper.TAccSmsaccountMapper;
import com.pl.model.TAccSmsaccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsMsgImpl implements SmsMsgService {

    @Autowired
    private TAccSmsaccountMapper tAccSmsaccountMapper;

    @Override
    public TAccSmsaccount getGeneralSituation(long companyId) {
        return tAccSmsaccountMapper.selectByPrimaryKey(companyId);
    }
}
