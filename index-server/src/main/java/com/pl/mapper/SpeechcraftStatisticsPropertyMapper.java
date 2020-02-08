package com.pl.mapper;

import com.pl.model.SpeechcraftStatisticsProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpeechcraftStatisticsPropertyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SpeechcraftStatisticsProperty record);

    int insertSelective(SpeechcraftStatisticsProperty record);

    SpeechcraftStatisticsProperty selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpeechcraftStatisticsProperty record);

    int updateByPrimaryKey(SpeechcraftStatisticsProperty record);

    List<SpeechcraftStatisticsProperty> selectByTableName(@Param("tableName") String tableName);
}