package com.pl.mapper;


import com.pl.model.wx.TContent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TContentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TContent record);

    int insertSelective(TContent record);

    TContent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TContent record);

    int updateByPrimaryKey(TContent record);

    List<TContent> selectContentList(@Param("cpName") String cpName,
                                     @Param("businessDesc") String businessDesc);

    TContent selectContentByBusinessId(Integer businessId);
}