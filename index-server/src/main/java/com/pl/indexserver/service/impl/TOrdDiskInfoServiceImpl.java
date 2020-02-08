package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.TOrdDiskInfoDto;
import com.pl.indexserver.service.TOrdDiskInfoService;
import com.pl.mapper.TOrdDiskInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class TOrdDiskInfoServiceImpl implements TOrdDiskInfoService {

    @Autowired
    private TOrdDiskInfoMapper tOrdDiskInfoMapper;

    @Override
    public TOrdDiskInfoDto selectByCompanyIdAndExpireTime(Long companyId, Integer status , String expireTtime) throws Exception {
        return tOrdDiskInfoMapper.selectByCompanyIdAndExpireTime(companyId.toString(),status.toString(),expireTtime);
    }
}
