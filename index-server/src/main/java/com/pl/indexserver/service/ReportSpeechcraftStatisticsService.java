package com.pl.indexserver.service;

import com.pl.model.ReportSpeechcraftStatistics;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 通用报表业务接口
 *
 * @Author bei.zhang
 * @Date 2018/9/13 17:54
 */
public interface ReportSpeechcraftStatisticsService {

    /**
     * 根据条件获取报表列表
     *
     * @param params
     * @return
     */
    List<ReportSpeechcraftStatistics> getReportSpeechcraftStatisticsByMap(Map<String, Object> params);

    /**
     * 导出excel报表
     *
     * @param tableName
     * @param reportData
     * @param response
     */
    void exportReport(String tableName, List<ReportSpeechcraftStatistics> reportData, HttpServletResponse response);

    public int insert(ReportSpeechcraftStatistics record);
    public void insertList(List<ReportSpeechcraftStatistics> record);
    
}
