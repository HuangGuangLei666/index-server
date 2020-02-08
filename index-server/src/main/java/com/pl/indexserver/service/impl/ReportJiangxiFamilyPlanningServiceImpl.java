package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.ReportJiangxiFamilyPlanningService;
import com.pl.indexserver.service.TDialogService;
import com.pl.indexserver.untils.ExportUtil;
import com.pl.indexserver.untils.ReportStatisticsStatus;
import com.pl.mapper.ReportJiangxiFamilyPlanningMapper;
import com.pl.model.ReportJiangxiFamilyPlanning;
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
public class ReportJiangxiFamilyPlanningServiceImpl implements ReportJiangxiFamilyPlanningService {

    @Autowired
    private ReportJiangxiFamilyPlanningMapper reportJiangxiFamilyPlanningMapper;
    @Autowired
    private TDialogService tDialogService;


    @Override
    public List<ReportJiangxiFamilyPlanning> getReportJiangxiFamilyPlaningByMap(Map<String, Object> params) {
        Long taskId = (Long) params.get("id");
        String postfix = tDialogService.getTablePostfix(taskId);
        params.put("postfix", postfix);
        return reportJiangxiFamilyPlanningMapper.selectReportFamilyPlaningByMap(params);
    }

    @Override
    public void exportReport(List<ReportJiangxiFamilyPlanning> reportData, HttpServletResponse response) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("江西计生委统计报表");
        CellStyle style = ExportUtil.getCellStyle(wb);
        String[] headers = {"姓名", "电话号码", "回访状态", "是否体检", "是否免费", "满意度"};
        ExportUtil.setAllHeader(wb, sheet, headers, style);
        for (int i = 0; i < reportData.size(); i++) {
            ReportJiangxiFamilyPlanning item = reportData.get(i);
            String[] cells = new String[]{item.getName(),
                    item.getPhone(),
                    ReportStatisticsStatus.converToName(item.getStatus()),
                    item.getMedicalExamination() + "",
                    item.getFree() + "",
                    item.getSatisfaction() + ""};
            ExportUtil.setAllCell(wb, sheet, i + 1, cells, style);
        }
        String fileName = "江西计生委统计报表";
        ExportUtil.exportXls(wb, fileName, response);
    }
}
