package com.pl.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.pl.model.ReportSpeechcraftStatistics;

public interface ReportSpeechcraftStatisticsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReportSpeechcraftStatistics record);
    
    void insertList(@Param ("list")  List <ReportSpeechcraftStatistics> list);

    int insertSelective(ReportSpeechcraftStatistics record);

    ReportSpeechcraftStatistics selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ReportSpeechcraftStatistics record);

    int updateByPrimaryKeyWithBLOBs(ReportSpeechcraftStatistics record);

    int updateByPrimaryKey(ReportSpeechcraftStatistics record);

    List<ReportSpeechcraftStatistics> selectReportSpeechcraftStatisticsByMap(Map<String, Object> params);
}