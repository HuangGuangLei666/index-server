package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.CallTaskStatDto;
import com.pl.indexserver.model.StatisticeModelDto;
import com.pl.indexserver.model.TDialogStatusInfoDto;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.query.TCustmIntentionQuery;
import com.pl.indexserver.service.CallTaskStatService;
import com.pl.indexserver.untils.DateUtils;
import com.pl.indexserver.untils.ExportUtil;
import com.pl.mapper.CallTaskStatMapper;
import com.pl.mapper.TCustmIntentionMapper;
import com.pl.model.Statistice;
import com.pl.model.TCustmIntention;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CallTaskStatServiceImpl implements CallTaskStatService {

    @Autowired
    private CallTaskStatMapper callTaskStatMapper;
    @Autowired
    private TCustmIntentionMapper tCustmIntentionMapper;

    @Override
    public CallTaskStatDto queryBasicStat(String beginDate, String endDate, String companyId, String type) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("beginTime", beginDate);
        params.put("endTime", endDate);
        params.put("companyId", companyId);
        params.put("type", type);
        return callTaskStatMapper.queryBasicStat(params);
    }

    @Override
    public List<CallTaskStatDto> queryTaskStat(String beginDate, String endDate, String companyId, String type) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("beginTime", beginDate);
        params.put("endTime", endDate);
        params.put("companyId", companyId);
        params.put("type", type);
        return callTaskStatMapper.queryTaskStat(params);
    }

    @Override
    public List<StatisticeModelDto> selectTaskByCompanyId(Long companyId) {
        List<StatisticeModelDto> dtoList = new ArrayList<StatisticeModelDto>();
        List<Statistice> list = callTaskStatMapper.queryAll(companyId);
        if (list.size() > 0) {
            for (Statistice st : list) {
                StatisticeModelDto dto = new StatisticeModelDto();
                dto.setTaskId(st.getTaskId());
                dto.setTaskName(st.getTaskName());
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public Page<StatisticeModelDto> selectMapStatisticeForIntention(TCustmIntentionQuery baseQuery) {
        List<StatisticeModelDto> dtoList = new ArrayList<>();
        Page<StatisticeModelDto> page = new Page<>();
        List<TCustmIntention> tCustmIntentions = tCustmIntentionMapper.selectByQuery(baseQuery);
        Long count = tCustmIntentionMapper.countByQuery(baseQuery);
        if (tCustmIntentions.size() <1){
            return null;
        }
        TDialogStatusInfoDto tDialogStatusInfoDto = new TDialogStatusInfoDto();
        Map<Integer, String> map1 = tDialogStatusInfoDto.getMap();
        if (tCustmIntentions.size() > 0) {
            for (int i = 0; i < tCustmIntentions.size(); i++) {
                StatisticeModelDto dto = new StatisticeModelDto();
//                Statistice se = list.get(i);
                TCustmIntention tCustmIntention = tCustmIntentions.get(i);
//                dto.setDiglogId(se.getDiglogId());
                dto.setCtName(tCustmIntention.getCustmName());
                dto.setCtPhone(tCustmIntention.getTelephone());
                dto.setDuration(tCustmIntention.getTotalSeconds());
                dto.setBeginDate(tCustmIntention.getBeginDate());
                dto.setOutNumber(tCustmIntention.getOutNumber());
                dto.setTaskId(tCustmIntention.getTaskId());
                dto.setTaskName(tCustmIntention.getTaskName());
                String intentionLevel = tCustmIntention.getIntentionLevel();
                // 根据type的不同，显示对应的等级
                if ("all".equals(baseQuery.getType())) {
                    String intention_level = StringUtils.isEmpty(tCustmIntention.getIntentionLevel())?"E":tCustmIntention.getIntentionLevel();
                    String focus_level = StringUtils.isEmpty(tCustmIntention.getFocusLevel())?"E":tCustmIntention.getFocusLevel();
                    String intent_level = StringUtils.isEmpty(tCustmIntention.getIntentLevel())?"E":tCustmIntention.getIntentLevel();
                    String temp = intention_level.compareTo(focus_level)>0?focus_level:intention_level;
                    intentionLevel = temp.compareTo(intent_level)>0?intent_level:temp;
                } else if ("focus".equals(baseQuery.getType())) {
                    intentionLevel = tCustmIntention.getFocusLevel();
                } else if ("intent".equals(baseQuery.getType())) {
                    intentionLevel = tCustmIntention.getIntentLevel();
                }

                switch (tCustmIntention.getStatus()) {
                    case 2:
                        if (StringUtils.isEmpty(intentionLevel)|| "E".equals(intentionLevel)) {
                            dto.setIntentionLevel("无意向");
                        } else {
                            dto.setIntentionLevel(intentionLevel);
                        }
                        dto.setIsIntentionInfo("已拨通");
                        dto.setIsIntention(2);
                        break;
                    case 1:
                        dto.setIsIntentionInfo("呼叫中");
                        dto.setIntentionLevel("未知意向");
                        dto.setIsIntention(1);
                        break;
                    default:
                        boolean contains = map1.containsKey(tCustmIntention.getStatus());
                        if (contains) {
                            dto.setIsIntention(tCustmIntention.getStatus());
                            dto.setIsIntentionInfo(map1.get(tCustmIntention.getStatus()));
                        } else {
                            dto.setIsIntentionInfo("未知状态");
                            dto.setIntentionLevel("未知意向");
                        }
                        break;
                }
                dtoList.add(dto);
            }
            page.setRecords(dtoList);
            page.setTotal(count);
        }
        return page;
    }

    @Override
    public void exportStatisticeForIntention(TCustmIntentionQuery baseQuery, HttpServletResponse response) {
        baseQuery.setLimit(false);//关闭分页
        List<StatisticeModelDto> dtoList = new ArrayList<>();

        List<TCustmIntention> tCustmIntentions = tCustmIntentionMapper.selectByQuery(baseQuery);
        TDialogStatusInfoDto tDialogStatusInfoDto = new TDialogStatusInfoDto();
        String fileName = "意向客户表";
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(fileName);
        CellStyle style = ExportUtil.getCellStyle(wb);
        String[] headers = {"客户姓名", "联系方式", "关注点-意向等级", "打分制-意向等级", "条件型-意向等级",
                "综合意向等级", "通话时间", "通话时长", "主叫号码", "外呼情况", "所属任务"};
        ExportUtil.setAllHeader(wb, sheet, headers, style);
        Map<Integer, String> taskStatusMap = tDialogStatusInfoDto.getMap();
        for (int i = 0; i < tCustmIntentions.size(); i++) {
            TCustmIntention t = tCustmIntentions.get(i);
            // 获取综合意向等级
            String intentionLevel = t.getIntentionLevel();
            String focusLevel = t.getFocusLevel();
            String intentLevel = t.getIntentLevel();
            String finalLevel = StringUtils.isEmpty(intentionLevel) ? null : intentionLevel;
            if (StringUtils.isEmpty(finalLevel)) {
                finalLevel = StringUtils.isEmpty(focusLevel) ? null : focusLevel;
            } else if (!StringUtils.isEmpty(focusLevel)) {
                finalLevel = finalLevel.compareTo(focusLevel) > 0 ? focusLevel : finalLevel;
            }
            if (StringUtils.isEmpty(finalLevel)) {
                finalLevel = StringUtils.isEmpty(intentLevel) ? null : intentLevel;
            } else if (!StringUtils.isEmpty(intentLevel)) {
                finalLevel = finalLevel.compareTo(intentLevel) > 0 ? intentLevel : finalLevel;
            }
            // 打分制意向等级
            intentionLevel = StringUtils.isEmpty(intentionLevel) ? "未知意向" : intentionLevel;
            intentionLevel = "E".equals(intentionLevel) ? "无意向" : intentionLevel;
            // 关注点意向等级
            focusLevel = StringUtils.isEmpty(focusLevel) ? "未知意向" : focusLevel;
            focusLevel = "E".equals(focusLevel) ? "无意向" : focusLevel;
            // 条件意向等级
            intentLevel = StringUtils.isEmpty(intentLevel) ? "未知意向" : intentLevel;
            intentLevel = "E".equals(intentLevel) ? "无意向" : intentLevel;
            // 综合意向等级
            finalLevel = StringUtils.isEmpty(finalLevel) ? "未知意向" : finalLevel;
            finalLevel = "E".equals(finalLevel) ? "无意向" : finalLevel;
            String[] cells = new String[]{
                    t.getCustmName(),
                    t.getTelephone(),
                    focusLevel,
                    intentionLevel,
                    intentLevel,
                    finalLevel,
                    StringUtils.isEmpty(t.getBeginDate()) ? "" : DateUtils.getStringForDate(t.getBeginDate(), DateUtils.DATEFORMAT_1),
                    ExportUtil.objectToString(t.getTotalSeconds()),
                    t.getOutNumber(),
                    taskStatusMap.containsKey(t.getStatus()) ? taskStatusMap.get(t.getStatus()) : "呼叫失败",
                    t.getTaskName()
            };
            ExportUtil.setAllCell(wb, sheet, i + 1, cells, style);
        }
        ExportUtil.exportXls(wb, fileName, response);
    }

    @Override
    public List<CallTaskStatDto> queryStatistics(Long companyId, Long businessId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("businessId", businessId);
        List<CallTaskStatDto> callTaskStats = callTaskStatMapper.queryStatistics(params);
        return callTaskStats;
    }

}
