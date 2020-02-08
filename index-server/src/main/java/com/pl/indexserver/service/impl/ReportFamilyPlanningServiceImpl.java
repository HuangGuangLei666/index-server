package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.ReportFamilyPlanningService;
import com.pl.indexserver.service.TDialogService;
import com.pl.indexserver.untils.ExportUtil;
import com.pl.indexserver.untils.FamilyPlanningStatus;
import com.pl.mapper.ReportFamilyPlanningMapper;
import com.pl.model.ReportFamilyPlanning;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 计划生育业务报表实现
 *
 * @Author bei.zhang
 * @Date 2018/9/13 17:55
 */
@Service
public class ReportFamilyPlanningServiceImpl implements ReportFamilyPlanningService {

    @Autowired
    private ReportFamilyPlanningMapper reportFamilyPlanningMapper;
    @Autowired
    private TDialogService tDialogService;


    @Override
    public List<ReportFamilyPlanning> getReportFamilyPlaningByMap(Map<String, Object> params) {
        Long taskId = (Long) params.get("id");
        String postfix = tDialogService.getTablePostfix(taskId);
        params.put("postfix", postfix);
        return reportFamilyPlanningMapper.selectReportFamilyPlaningByMap(params);
    }

    @Override
    public void exportReport(List<ReportFamilyPlanning> reportData, HttpServletResponse response) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("计划生育统计报表");
        CellStyle style = ExportUtil.getCellStyle(wb);
        String[] headers = {"县市区编码", "县市区名称", "电话号码", "接通状况", "姓氏称呼(女士/先生）", "性别", "年龄", "终止妊娠", "奖励扶助", "两孩政策"};
        ExportUtil.setAllHeader(wb, sheet, headers, style);
        for (int i = 0; i < reportData.size(); i++) {
            ReportFamilyPlanning item = reportData.get(i);
            String sex = "未知";
            if ("woman".equals(item.getSex())) {
                sex = "女";
            } else if ("man".equals(item.getSex())) {
                sex = "男";
            }
            String[] cells = new String[]{item.getCountyCode(), item.getCountyName(), item.getPhone(),
                    FamilyPlanningStatus.converToName(item.getStatus()),
                    "unknown".equals(item.getName()) ? "未知" : item.getName(),
                    sex,
                    "unknown".equals(item.getAge()) ? "未知" : item.getAge(),
                    item.getTerminalPregnancy() + "",
                    item.getRewardHelp() + "",
                    item.getTwoChildPolicy() + ""};
            ExportUtil.setAllCell(wb, sheet, i + 1, cells, style);
        }
        String fileName = "计划生育报表";
        ExportUtil.exportXls(wb, fileName, response);
    }

}
