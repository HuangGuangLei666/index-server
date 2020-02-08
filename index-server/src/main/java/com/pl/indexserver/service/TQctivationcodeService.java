package com.pl.indexserver.service;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.model.wx.TQctivationcode;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/10
 */
public interface TQctivationcodeService {
    void addTQctivationcode(TQctivationcode tQctivationcode);

    TQctivationcode selectByCode(String activationCode);

    int openMembershipByCode(String code, Integer userId);

    TQctivationcode selectByTradeNo(String ordersSn);

    List<TQctivationcode> selectByUserIdAndStatus(Integer userId);

    CheckSmsCodeResp openMemberByCode(String code, Integer userId);
}
