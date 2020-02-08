package com.pl.mapper;

import com.pl.model.TBusinessProperty;
import org.apache.ibatis.annotations.Param;

public interface TBusinessPropertyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TBusinessProperty record);

    int insertSelective(TBusinessProperty record);

    TBusinessProperty selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TBusinessProperty record);

    int updateByPrimaryKey(TBusinessProperty record);

    TBusinessProperty selectByBusinessIdAndType(@Param("businessId") Long businessId, @Param("propertyType") String propertyType);
}