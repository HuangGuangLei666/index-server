package com.pl.indexserver.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.BusinessPropertyType;
import com.pl.indexserver.untils.DateUtils;
import com.pl.indexserver.untils.WavMergeUtil;
import com.pl.mapper.CallTaskMapper;
import com.pl.mapper.SpeechcraftStatisticsPropertyMapper;
import com.pl.mapper.TBusinessPropertyMapper;
import com.pl.model.*;
import com.pl.thirdparty.api.VoiceGenderService;
/*import com.pl.thirdparty.dto.enums.GenderType;
import com.voiceai.vprcjava.MThrowable;
import com.voiceai.vprcjava.VPRCNet;*/
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * 智库报表业务实现
 *
 * @Author bei.zhang
 * @Date 2018/9/13 11:17
 */
@Service
public class BusinessReportServiceImpl implements BusinessReportService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessReportServiceImpl.class);

    @Autowired
    private TBusinessPropertyMapper tBusinessPropertyMapper;
    @Autowired
    private CallTaskMapper callTaskMapper;
    @Autowired
    private ReportFamilyPlanningService reportFamilyPlanningService;
    @Autowired
    private ReportJiangxiFamilyPlanningService reportJiangxiFamilyPlanningService;
    @Autowired
    private ReportSpeechcraftStatisticsService reportSpeechcraftStatisticsService;

    @Autowired
    private SpeechcraftStatisticsPropertyMapper speechcraftStatisticsPropertyMapper;

    @Autowired
    private TDialogService tDialogService;

    @Reference(version = "${thirdparty.service.version}",
            application = "${dubbo.application.id}")
    private VoiceGenderService voiceGenderService;


    private static final String STATISTICS_VALUE = "statistics";
    private static final String STATISTICS_TABLE_KEY = "statistics_table";
    private static final String STATISTICS_KEY = "type";

    private static final String REPORT_FAMILY_PLANNING_VALUE = "report_family_planning";
    private static final String REPORT_JIANGXI_FAMILY_PLANNING_VALUE = "report_jiangxi_family_planning";


    @Override
    public int getVisitSuccessCount(Long companyId, Long taskId) {
        CallTask callTask = callTaskMapper.selectByPrimaryKey(taskId);
        TBusinessProperty tBusinessProperty = tBusinessPropertyMapper.selectByBusinessIdAndType(callTask.getBusinessId(), BusinessPropertyType.BUSINESS_TYPE.getCode());
        if (tBusinessProperty != null) {
            JSONObject propertyJson = JSON.parseObject(tBusinessProperty.getPropertyValue());
            if (STATISTICS_VALUE.equalsIgnoreCase(propertyJson.getString(STATISTICS_KEY))) {
                if (REPORT_FAMILY_PLANNING_VALUE.equalsIgnoreCase(propertyJson.getString(STATISTICS_TABLE_KEY))) {
                    return tDialogService.getVisitSuccessReportFamilyPlaningCount(companyId, taskId);
                } else if (REPORT_JIANGXI_FAMILY_PLANNING_VALUE.equalsIgnoreCase(propertyJson.getString(STATISTICS_TABLE_KEY))) {
                    return tDialogService.getVisitSuccessReportJiangxiFamilyPlaningCount(companyId, taskId);
                } else {
                    return tDialogService.getVisitSuccessReportSpeechcraftStatisticsCount(companyId, taskId);
                }
            }
        }
        return 0;
    }

    @Override
    public String getReportTableName(Long taskId) {
        CallTask callTask = callTaskMapper.selectByPrimaryKey(taskId);
        TBusinessProperty tBusinessProperty = tBusinessPropertyMapper.selectByBusinessIdAndType(callTask.getBusinessId(), BusinessPropertyType.BUSINESS_TYPE.getCode());
        if (tBusinessProperty != null) {
            JSONObject propertyJson = JSON.parseObject(tBusinessProperty.getPropertyValue());
            if (STATISTICS_VALUE.equalsIgnoreCase(propertyJson.getString(STATISTICS_KEY))) {
                return propertyJson.getString(STATISTICS_TABLE_KEY);
            }
        }
        return null;
    }

    @Override
    public boolean checkBusinessStatistics(Long businessId) {
        TBusinessProperty tBusinessProperty = tBusinessPropertyMapper.selectByBusinessIdAndType(businessId, BusinessPropertyType.BUSINESS_TYPE.getCode());
        if (null == tBusinessProperty) {
            return false;
        }
        JSONObject propertyJson = JSON.parseObject(tBusinessProperty.getPropertyValue());
        return STATISTICS_VALUE.equalsIgnoreCase(propertyJson.getString(STATISTICS_KEY));

    }

    @Override
    public void exportBusinessReport(Long taskId, Map<String, Object> params, HttpServletResponse response) {
        CallTask callTask = callTaskMapper.selectByPrimaryKey(taskId);
        TBusinessProperty tBusinessProperty = tBusinessPropertyMapper.selectByBusinessIdAndType(callTask.getBusinessId(), BusinessPropertyType.BUSINESS_TYPE.getCode());
        if (tBusinessProperty != null) {
            JSONObject propertyJson = JSON.parseObject(tBusinessProperty.getPropertyValue());
            if (STATISTICS_VALUE.equalsIgnoreCase(propertyJson.getString(STATISTICS_KEY))) {
                if (REPORT_FAMILY_PLANNING_VALUE.equalsIgnoreCase(propertyJson.getString(STATISTICS_TABLE_KEY))) {
                    List<ReportFamilyPlanning> dataList = reportFamilyPlanningService.getReportFamilyPlaningByMap(params);
                    reportFamilyPlanningService.exportReport(dataList, response);
                } else if (REPORT_JIANGXI_FAMILY_PLANNING_VALUE.equalsIgnoreCase(propertyJson.getString(STATISTICS_TABLE_KEY))) {
                    List<ReportJiangxiFamilyPlanning> dataList = reportJiangxiFamilyPlanningService.getReportJiangxiFamilyPlaningByMap(params);
                    reportJiangxiFamilyPlanningService.exportReport(dataList, response);
                } else {
                	long t = System.currentTimeMillis();
                	//同一时刻只有一个生成报表数据
                	synchronized (this.getClass())
                	{
                		genReportDate(callTask, propertyJson.getString(STATISTICS_TABLE_KEY));
                	}
                    logger.info("=====cost time1 = " + (System.currentTimeMillis() - t));
                    List<ReportSpeechcraftStatistics> dataList = reportSpeechcraftStatisticsService.getReportSpeechcraftStatisticsByMap(params);
                    logger.info("=====cost time2 = " + (System.currentTimeMillis() - t));
                    reportSpeechcraftStatisticsService.exportReport(propertyJson.getString(STATISTICS_TABLE_KEY), dataList, response);
                    logger.info("=====cost time3 = " + (System.currentTimeMillis() - t));
                }
            }
        }
    }

    public static final Map<Integer, String> DIALOG_CALL_STATUS = new HashMap<>();

    static {
        DIALOG_CALL_STATUS.put(2, "RECORD");
        DIALOG_CALL_STATUS.put(10, "WRONG_NUMBER");
        DIALOG_CALL_STATUS.put(11, "FAILED");
        DIALOG_CALL_STATUS.put(13, "NUM_NOT_FOUND");
        DIALOG_CALL_STATUS.put(14, "POWER_OFF");
        DIALOG_CALL_STATUS.put(15, "BUSY");
        DIALOG_CALL_STATUS.put(16, "STOPED");
        DIALOG_CALL_STATUS.put(17, "NO_RESPONSE");
        DIALOG_CALL_STATUS.put(18, "PAUSED");
        DIALOG_CALL_STATUS.put(19, "TIMEOUT");
        DIALOG_CALL_STATUS.put(22, "NOT_FOUND");
        DIALOG_CALL_STATUS.put(30, "FAILED");
    }

    private void genReportDate(CallTask callTask, String tableName) {

        List<TDialog> list = tDialogService.getTDialogListToStat(callTask.getCompanyId(), callTask.getId());
        List<SpeechcraftStatisticsProperty> properties = speechcraftStatisticsPropertyMapper.selectByTableName(tableName);
        List<ReportSpeechcraftStatistics> listReportSpeechcraftStatistics = new ArrayList<ReportSpeechcraftStatistics>();
        for (TDialog d : list) {
            ReportSpeechcraftStatistics record = new ReportSpeechcraftStatistics();
            listReportSpeechcraftStatistics.add(record);
            record.setBusinessId(callTask.getBusinessId());
            record.setCompanyId(callTask.getCompanyId());
            record.setCreateBy("system");
            record.setCreateDate(new Date());
            record.setDialogId(d.getId());
            record.setStatus(DIALOG_CALL_STATUS.get(d.getStatus()));
            record.setTaskId(callTask.getId());
            record.setTelephone(d.getTelephone());

            JSONObject result = new JSONObject();

            for (SpeechcraftStatisticsProperty p : properties) {
                String value = "";
                String propertyKey = p.getPropertyKey();
                try {
                    if ("EXTRA".equalsIgnoreCase(p.getCraftType())) {
                        if ("telephone".equals(propertyKey)) {
                            value = d.getTelephone();
                        }
                        if ("status".equals(propertyKey)) {
                            value = "" + d.getStatus();
                        }
                        if ("totalSeconds".equals(propertyKey)) {
                            value = "" + d.getTotal_seconds();
                        }
                        if ("beginDate".equals(propertyKey)) {
                            value = DateUtils.getStringForDate(d.getBeginDate(), DateUtils.DATEFORMAT_1);
                        }

                        if ("vioceSex".equals(propertyKey)) {
                            logger.info("=======日期={}======电话={}======",DateUtils.getStringForDate(d.getBeginDate(), "yyyyMMdd"),
                                    d.getTelephone());
                            value = getSex(DateUtils.getStringForDate(d.getBeginDate(), "yyyyMMdd"),d.getTelephone());
                        }

                        if ("intentLevel".equals(propertyKey)) {
                            value = "" + d.getIntentLevel();
                        }
                        if ("hangup".equals(propertyKey)) {
                            String[] arr = p.getCraftId().split(":");
                            if ("recv_bye".equals(d.getErrormsg())) {
                                value = arr[0];
                            } else if ("send_bye".equals(d.getErrormsg())) {
                                value = arr[1];
                            } else {
                                value = "呼叫失败";
                            }
                        }
                        if (propertyKey.startsWith("totalSeconds_GT")) {
                            String numStr = propertyKey.substring(15);
                            int num = Integer.parseInt(numStr);
                            String[] arr = p.getCraftId().split(":");
                            if (d.getTotal_seconds() > num) {
                                value = arr[0];
                            } else {
                                value = arr[1];
                            }
                        }
                        if (propertyKey.startsWith("totalSeconds_LT")) {
                            String numStr = propertyKey.substring(15);
                            int num = Integer.parseInt(numStr);
                            String[] arr = p.getCraftId().split(":");
                            if (d.getTotal_seconds() < num) {
                                value = arr[0];
                            } else {
                                value = arr[1];
                            }
                        }
                        if (propertyKey.startsWith("input")) {
                            String inputPropName = propertyKey.substring(6);
                            JSONObject obj = JSONObject.parseObject(d.getTtsInfo());
                            value = obj.getString(inputPropName);
                        }
                        if (propertyKey.startsWith("ans")) {
                            JSONArray objArr = JSONArray.parseArray(d.getFile_path());
                            for (int i = objArr.size()-1; i>0 ; i--) {
                                try {
                                    JSONObject obj = JSONObject.parseObject(objArr.get(i).toString());
                                    String contentCustomer = obj.getString("content_customer");
                                    String info_map = obj.getString("info_map");
                                    JSONObject obj1 = JSONObject.parseObject(info_map);
                                    String craftId = obj1.getString("returnMsg.getWorkNodeId()");
                                    logger.info("=1========{}---{}",contentCustomer, craftId );
                                    if (!StringUtils.isEmpty(contentCustomer) && !StringUtils.isEmpty(craftId)) {
                                        //contentCustomer=["","",""]。去除[],保留里面的一个个元素,取第一个输出
                                        String substring = contentCustomer.substring(1, contentCustomer.length() - 1);
                                        String[] split = substring.split(",");
                                        logger.info("=2========{}---{} ,{}" +contentCustomer, craftId,propertyKey );
                                        if (propertyKey.length() > 17) {
                                            String ansCrafIds = propertyKey.substring(4);
                                            String[] splits = ansCrafIds.split("_");
                                            logger.info("===========拆分的节点参数split={}", splits);
                                            for (String s : splits) {
                                                if (s.equals(craftId)) {
                                                    value = split[0];
                                                    logger.info("=========节点参数遍历的结果s={},value={}", s, value);
                                                    break;
                                                }
                                            }
                                        } else {
                                            String ansCraftId = propertyKey.substring(4);
                                            if (ansCraftId.equals(craftId)) {
                                                value = split[0];
                                                break;
                                            }
                                        }

                                    }

                                } catch (Exception e) {
                                    logger.info("报表通话参数异常", e);
                                }
                            }
                        }

                        //福建移动关怀成功
                        if (propertyKey.startsWith("care")) {
                            JSONArray objArr = JSONArray.parseArray(d.getFile_path());
                            String jsonStr = objArr.toString();
                            if (propertyKey.length() > 18){
                                String substring = propertyKey.substring(5);
                                String[] split = substring.split("_");
                                for (String s : split) {
                                    if (jsonStr.contains(s)){
                                        String craftId = p.getCraftId();
                                        String[] sp = craftId.split(":");
                                        value = sp[1];
                                    }
                                }
                            }
                            else {
                                String substring = propertyKey.substring(5);
                                if (jsonStr.contains(substring)) {
                                    String craftId = p.getCraftId();
                                    String[] split = craftId.split(":");
                                    value = split[1];
                                }
                            }
                        }

                        if (propertyKey.startsWith("dia")) {
                            JSONArray objArr = JSONArray.parseArray(d.getFile_path());
                            for (int i = 1; i < objArr.size(); i++) {
                                try {
                                    JSONObject obj = JSONObject.parseObject(objArr.get(i).toString());
                                    String infoMap = obj.getString("info_map");
                                    JSONObject infoMapJson = JSONObject.parseObject(infoMap);
                                    String craftId = infoMapJson.getString("chatInfo.getWorkNodeId()");
                                    String algorithmContent = infoMapJson.getString("algorithmContent");
                                    if (algorithmContent != null) {
                                        JSONObject algJson = JSONObject.parseObject(algorithmContent);
                                        String respId = algJson.getString("resp_id");

                                        if (respId == null) {
                                            continue;
                                        }
                                        logger.info("话术id=" + craftId + "；" + "算法返回的json值=" + algJson.toString());
                                        //两个节点id下的情况

                                        if (propertyKey.length() > 17) {
                                            String diaCraftId = propertyKey.substring(18);
                                            String diaCraftId2 = propertyKey.substring(4, 17);
                                            if (diaCraftId.equals(craftId) || diaCraftId2.equals(craftId)) {
                                                try {
                                                    String[] arr = p.getCraftId().split(",");
                                                    for (String s : arr) {
                                                        if (s.contains(respId)) {
                                                            value = s.split(":")[1];
                                                            logger.info("s的值为============{},arr的值为============{}", s, arr);
                                                            break;
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    logger.info("arr报空指针异常", e);
                                                }
                                            }
                                        } else {
                                            String diaCraftId = propertyKey.substring(4);
                                            //一个节点id下的情况
                                            if (diaCraftId.equals(craftId)) {

                                                String[] arr = p.getCraftId().split(",");
                                                try {
                                                    for (String s : arr) {
                                                        if (s.contains(respId)) {
                                                            value = s.split(":")[1];
                                                            logger.info("s2的值为============{},arr2的值为============{}", s, arr);
                                                            break;
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    logger.info(algJson + "-----" + respId + "----" + p + "=====arr2报空指针异常" + p.getCraftId(), e);
                                                }
                                            }
                                        }

                                    }
                                } catch (Exception e) {
                                    logger.info("报表通话参数异常", e);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.info("报表全局变量异常", e);
                }
                result.put(propertyKey, value);
            }

            record.setResult(result.toJSONString());
            logger.info("====d.id=" + d.getId() + "  " + result.toJSONString());
            //reportSpeechcraftStatisticsService.insert(record);
        }
        List<ReportSpeechcraftStatistics> oneTimes = new  ArrayList<ReportSpeechcraftStatistics>();
         
        int count = 0;
        for (ReportSpeechcraftStatistics e : listReportSpeechcraftStatistics )
        {
        	count ++;
        	oneTimes.add(e);
        	if (count > 1000)
        	{
        		reportSpeechcraftStatisticsService.insertList(oneTimes);
        		logger.info(" reportSpeechcraftStatisticsService.insertList.size="+ oneTimes.size());
        		oneTimes.clear();
        		count = 0;
        	}
        	
        }
        if (oneTimes.size()>0)
        {
        	reportSpeechcraftStatisticsService.insertList(oneTimes);
        	logger.info("last reportSpeechcraftStatisticsService.insertList.size="+ oneTimes.size());
    		
        }
    }

    public String getSex(String beginDate,String phoneNumber){
        String path = "/mnt/tm/recording_sections/" + beginDate;
        String sex = "未知";
        File file = new File(path);
        List<File> fileList= new ArrayList<>();
        for (File f: file.listFiles() ) {
            if (f.getName().startsWith(phoneNumber)) {
                fileList.add(f);
            }
        }
        logger.info("======fileList={}===========", fileList.size());
        if (fileList.size() > 0) {

            File tempFile = new File("tmpFile.wav");
            File[] files = new File[fileList.size()];
            logger.info("=======files={}==============", files);

            for (int i = 0; i < fileList.size(); i++) {
                files[i] = fileList.get(i);
            }

            try {
                logger.info("=======files={}==============", files.length);
                WavMergeUtil.mergeWav(files, tempFile.getAbsolutePath());
                FileInputStream is = new FileInputStream(new File(tempFile.getAbsolutePath()));
                byte[] bytes = IOUtils.toByteArray(is);

//                sex = voiceGenderService.genderRecognition(bytes);
                logger.info("==========sex=========="+sex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sex;
    }

}
