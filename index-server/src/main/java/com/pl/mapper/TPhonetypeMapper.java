package com.pl.mapper;


import com.pl.model.wx.InterceptSta;
import com.pl.model.wx.TPhonetype;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TPhonetypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TPhonetype record);

    int insertSelective(TPhonetype record);

    TPhonetype selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TPhonetype record);

    int updateByPrimaryKey(TPhonetype record);

    int addPhoneType(TPhonetype tPhonetype);

    List<TPhonetype> selectByOpenidAndType(@Param("openid") String openid, @Param("type")String type);

    TPhonetype selectByOpenidAndPhone(@Param("openid")String openid, @Param("callerPhone")String callerPhone);

    void insertInterceptPhone(InterceptSta interceptSta);

    List<InterceptSta> selectIntercepCount(@Param("phonenumber")String phonenumber, @Param("format")String format);
}