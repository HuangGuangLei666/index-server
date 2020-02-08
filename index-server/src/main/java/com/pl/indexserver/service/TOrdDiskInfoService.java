package com.pl.indexserver.service;

import com.pl.indexserver.model.TOrdDiskInfoDto;

import java.util.Date;
import java.util.Map;

public interface TOrdDiskInfoService {

    /**
     * 统计对应公司交易成功且未过期的磁盘空间
     *
     * @param companyId   公司id
     * @param status      订单状态
     * @param expireTtime 过期时间
     * @return 返回查询到的结果
     * @throws Exception
     */
    TOrdDiskInfoDto selectByCompanyIdAndExpireTime(Long companyId, Integer status, String expireTtime) throws Exception;
}