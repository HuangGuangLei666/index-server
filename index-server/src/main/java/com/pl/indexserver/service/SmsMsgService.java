package com.pl.indexserver.service;

import com.pl.model.TAccSmsaccount;


/**
 * 账户中心，短信有关信息接口定义。
 */
public interface SmsMsgService {
    //账户信息
    TAccSmsaccount getGeneralSituation(long companyId);
}
