package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TCustomerrespService;
import com.pl.mapper.TCustomrespMapper;
import com.pl.mapper.TmCustomerMapper;
import com.pl.model.wx.TCustomresp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/28 15:34
 * @Description
 */
@Service
public class TCustomerrespServiceImpl implements TCustomerrespService {

    @Autowired
    private TCustomrespMapper tCustomrespMapper;

    @Override
    public int addCustomerresp(TCustomresp tCustomresp) {
        return tCustomrespMapper.addCustomerresp(tCustomresp);
    }

    @Override
    public int deleteCustomerresp(Integer id) {
        return tCustomrespMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<TCustomresp> selectByOpenidAndKnowledgeId(String openid, String knowledgeId) {
        return tCustomrespMapper.selectByOpenidAndKnowledgeId(openid,knowledgeId);
    }

    @Override
    public TCustomresp selectByOpenidAndKnowId(Integer answerId,String openid, String knowledgeId) {
        return tCustomrespMapper.selectByOpenidAndKnowId(answerId,openid,knowledgeId);
    }
}
