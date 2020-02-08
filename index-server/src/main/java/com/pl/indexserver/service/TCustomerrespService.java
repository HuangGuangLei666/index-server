package com.pl.indexserver.service;

import com.pl.model.wx.TCustomresp;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/28 15:33
 * @Description
 */
public interface TCustomerrespService {
    int addCustomerresp(TCustomresp tCustomresp);

    int deleteCustomerresp(Integer id);

    List<TCustomresp> selectByOpenidAndKnowledgeId(String openid, String knowledgeId);

    TCustomresp selectByOpenidAndKnowId(Integer answerId,String openid, String knowledgeId);
}
