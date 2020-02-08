package com.pl.mapper;


import com.pl.model.wx.TUserQa;
import org.apache.ibatis.annotations.Param;

public interface TUserQaMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TUserQa record);

    int insertSelective(TUserQa record);

    TUserQa selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TUserQa record);

    int updateByPrimaryKey(TUserQa record);

    int addCustomerresp(TUserQa tUserQa);

    TUserQa selectAnswerId(@Param("knowledgeId") String knowledgeId, @Param("openid")String openid);
}