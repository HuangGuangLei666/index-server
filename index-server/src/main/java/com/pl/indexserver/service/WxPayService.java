package com.pl.indexserver.service;

import com.pl.model.wx.TQctivationcode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface WxPayService {
    Map<String,Object> openMembershipPay(HttpServletRequest request, String totalFee, Integer goodsId, String unionid);

    void openMembershipPayCallback(HttpServletRequest request, HttpServletResponse response);

    Map<String,Object> giveSecretaryPay(HttpServletRequest request, Integer userId, Integer goodsId, String unionid);

    TQctivationcode giveSecretaryPayCallback(HttpServletRequest request, HttpServletResponse response);
}
