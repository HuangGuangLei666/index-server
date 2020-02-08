package com.pl.mapper;


import com.pl.model.wx.TCustomresp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TCustomrespMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TCustomresp record);

    int insertSelective(TCustomresp record);

    TCustomresp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TCustomresp record);

    int updateByPrimaryKey(TCustomresp record);

    int addCustomerresp(TCustomresp tCustomresp);

    List<TCustomresp> selectByOpenidAndKnowledgeId(@Param("openid") String openid, @Param("knowledgeId")String knowledgeId);

    TCustomresp selectByOpenidAndKnowId(@Param("answerId")Integer answer,@Param("openid")String openid, @Param("knowledgeId")String knowledgeId);
}