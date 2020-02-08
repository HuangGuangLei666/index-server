package com.pl.indexserver.service.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.pl.indexserver.service.ReportSpeechcraftStatisticsService;
import com.pl.indexserver.service.TDialogService;
import com.pl.indexserver.untils.ExportUtil;
import com.pl.indexserver.untils.ReportStatisticsStatus;
import com.pl.mapper.ReportSpeechcraftStatisticsMapper;
import com.pl.mapper.SpeechcraftStatisticsPropertyMapper;
import com.pl.model.ReportSpeechcraftStatistics;
import com.pl.model.SpeechcraftStatisticsProperty;

/**
 * 计划生育业务报表实现
 *
 * @Author bei.zhang
 * @Date 2018/9/13 17:55
 */
@Service
public class ReportSpeechcraftStatisticsServiceImpl implements ReportSpeechcraftStatisticsService {

    @Autowired
    private ReportSpeechcraftStatisticsMapper reportSpeechcraftStatisticsMapper;
    @Autowired
    private SpeechcraftStatisticsPropertyMapper speechcraftStatisticsPropertyMapper;
    @Autowired
    private TDialogService tDialogService;


    @Override
    public List<ReportSpeechcraftStatistics> getReportSpeechcraftStatisticsByMap(Map<String, Object> params) {
        Long taskId = (Long) params.get("id");
        String postfix = tDialogService.getTablePostfix(taskId);
        params.put("postfix", postfix);
        return reportSpeechcraftStatisticsMapper.selectReportSpeechcraftStatisticsByMap(params);
    }
    
    
    @Override
    public int insert(ReportSpeechcraftStatistics record){
        
        return reportSpeechcraftStatisticsMapper.insert(record);
    }
    
    @Override
    public void insertList(List <ReportSpeechcraftStatistics> list){
        
         reportSpeechcraftStatisticsMapper.insertList(list);
    }
    
  

    @Override
    public void exportReport(String tableName, List<ReportSpeechcraftStatistics> reportData, HttpServletResponse response) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("回访统计报表");
        CellStyle style = ExportUtil.getCellStyle(wb);
        List<SpeechcraftStatisticsProperty> properties = speechcraftStatisticsPropertyMapper.selectByTableName(tableName);
        Set<String> headerSet = new LinkedHashSet<>();
        Set<String> propertyKeySet = new LinkedHashSet<>();
        for (int i = 0; i < properties.size(); i++) {
            headerSet.add(properties.get(i).getPropertyName());
            propertyKeySet.add(properties.get(i).getPropertyKey());
        }
        String[] headers = headerSet.toArray(new String[headerSet.size()]);
        String[] propertyKeys = propertyKeySet.toArray(new String[propertyKeySet.size()]);
        ExportUtil.setAllHeader(wb, sheet, headers, style);
        for (int i = 0; i < reportData.size(); i++) {
            ReportSpeechcraftStatistics item = reportData.get(i);
            Map<String, String> result = (Map) JSON.parse(item.getResult());
            if (result == null) {
                continue;
            }
            String[] cells = new String[propertyKeys.length];
            for (int j = 0; j < propertyKeys.length; j++) {
                String value = result.get(propertyKeys[j]);
                if ("sex".equals(propertyKeys[j])) {
                    if ("woman".equals(value)) {
                        cells[j] = "女";
                    } else if ("man".equals(value)) {
                        cells[j] = "男";
                    } else {
                        cells[j] = "未知";
                    }
                } else if ("status".equals(propertyKeys[j])) {
                    cells[j] = ReportStatisticsStatus.converToName(item.getStatus());
                } else if ("unknown".equals(value)) {
                    cells[j] = "未知";
                } else {
                    cells[j] = value;
                }
            }
            ExportUtil.setAllCell(wb, sheet, i + 1, cells, style);
        }
        String fileName = "回访统计报表";
        ExportUtil.exportXls(wb, fileName, response);
    }

}
