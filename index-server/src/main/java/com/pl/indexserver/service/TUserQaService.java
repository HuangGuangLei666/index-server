package com.pl.indexserver.service;

import com.pl.model.wx.TUserQa; /**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 17:42
 * @Description
 */
public interface TUserQaService {

    int addCustomerresp(TUserQa tUserQa);

    TUserQa selectAnswerId(String knowledgeId, String openid);
}
