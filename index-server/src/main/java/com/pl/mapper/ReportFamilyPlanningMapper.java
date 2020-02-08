package com.pl.mapper;

import com.pl.model.ReportFamilyPlanning;

import java.util.List;
import java.util.Map;

public interface ReportFamilyPlanningMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReportFamilyPlanning record);

    int insertSelective(ReportFamilyPlanning record);

    ReportFamilyPlanning selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ReportFamilyPlanning record);

    int updateByPrimaryKey(ReportFamilyPlanning record);

    List<ReportFamilyPlanning> selectReportFamilyPlaningByMap(Map<String, Object> params);
}