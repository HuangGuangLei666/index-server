package com.pl.mapper;

import com.pl.model.ReportJiangxiFamilyPlanning;

import java.util.List;
import java.util.Map;

public interface ReportJiangxiFamilyPlanningMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReportJiangxiFamilyPlanning record);

    int insertSelective(ReportJiangxiFamilyPlanning record);

    ReportJiangxiFamilyPlanning selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ReportJiangxiFamilyPlanning record);

    int updateByPrimaryKey(ReportJiangxiFamilyPlanning record);

    List<ReportJiangxiFamilyPlanning> selectReportFamilyPlaningByMap(Map<String, Object> params);
}