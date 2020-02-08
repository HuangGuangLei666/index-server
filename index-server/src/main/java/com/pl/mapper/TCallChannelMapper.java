package com.pl.mapper;

import com.pl.model.TCallChannel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TCallChannelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TCallChannel record);

    int insertSelective(TCallChannel record);

    TCallChannel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TCallChannel record);

    int updateByPrimaryKey(TCallChannel record);

    List<TCallChannel> getCallChannelNameByCompany_id(@Param("company_id") Long company_id);

}