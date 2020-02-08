package com.pl.indexserver.service;

import com.pl.model.wx.TMechanism; /**
 * @author HuangGuangLei
 * @Date 2019/11/8
 */
public interface TMechanismService {
    int insertOrgApply(TMechanism tMechanism);

    TMechanism selectByOrgNum(String code, String orgNum);

    TMechanism selectByOpenId(String openId);
}
