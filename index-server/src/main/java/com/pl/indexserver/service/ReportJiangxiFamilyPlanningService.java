package com.pl.indexserver.service;

import com.pl.model.ReportJiangxiFamilyPlanning;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 江西计生委报表业务接口
 *
 * @Author bei.zhang
 * @Date 2018/9/13 17:54
 */
public interface ReportJiangxiFamilyPlanningService {

    /**
     * 根据条件获取报表列表
     *
     * @param params
     * @return
     */
    List<ReportJiangxiFamilyPlanning> getReportJiangxiFamilyPlaningByMap(Map<String, Object> params);

    /**
     * 导出excel报表
     *
     * @param reportData
     */
    void exportReport(List<ReportJiangxiFamilyPlanning> reportData, HttpServletResponse response);

}
