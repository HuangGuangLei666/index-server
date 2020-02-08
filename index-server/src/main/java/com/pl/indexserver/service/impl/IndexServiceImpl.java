package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.CallTaskIndexDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.CallTaskService;
import com.pl.indexserver.service.IndexService;
import com.pl.indexserver.untils.DateUtils;
import com.pl.mapper.TCallAgentMapper;
import com.pl.mapper.TDialogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private CallTaskService callTaskService;

    @Autowired
    private TCallAgentMapper tCallAgentMapper;

    @Autowired
    private TDialogMapper tDialogMapper;

    @Override
    public ReturnMsg getIndexPageData(Long companyId, String uid)  {
        //今日
        Map<String,Integer> map1 = callTaskService.getSum_CallTaskAndCalls(companyId,uid,DateUtils.getCurrentDate());
        //昨日
        Map<String,Integer> map2 = callTaskService.getSum_CallTaskAndCalls(companyId,uid,DateUtils.getBackUpDate());
        //空闲坐席
        int usableAgent = tCallAgentMapper.getUsableAgentByCompanyId(companyId);
        String profix = DateUtils.getStringForDate(DateUtils.getCurrentDate(),"yyyyMM");
        //当月总呼叫时长。
        long callTimeSum = tDialogMapper.callTimeSumByCompanyId(profix,companyId);
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        Map<String, Object>  maps = new HashMap<>();
        maps.put("todaySum",map1);
        maps.put("yesterdaySum",map2);
        maps.put("freeAgent",usableAgent);
        maps.put("callTimeLong",df.format((float)callTimeSum/3600));
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setCode(0);
        returnMsg.setContent(maps);
        return returnMsg;
    }

    @Override
    public ReturnMsg getIndexPageData2(Integer type, Long companyId, String uid) {

        String profix = DateUtils.getStringForDate(DateUtils.getCurrentDate(),"yyyyMM");
        //外呼量
        Date starTime = DateUtils.getCurrentDate();
        Date endTime;
        List<CallTaskIndexDto> callTaskIndexDtos = new ArrayList<>();
        switch (type){
            case 11: //最近7日外呼量
                endTime =DateUtils.getBackUp7(starTime);
                callTaskIndexDtos = callTaskService.getCallTaskCount(companyId,starTime,endTime);
                break;
            case 12: //最近30日外呼量
                endTime =DateUtils.getBackUp30(starTime);
                callTaskIndexDtos = callTaskService.getCallTaskCount(companyId,starTime,endTime);
                break;
            case 21: //最近7日接通量
                endTime =DateUtils.getBackUp7(starTime);
                callTaskIndexDtos = tDialogMapper.getCallRecordsOnlineByCompanyId(profix,companyId,starTime,endTime);
                break;
            case 22: //最近30日接通量
                endTime =DateUtils.getBackUp30(starTime);
                callTaskIndexDtos = tDialogMapper.getCallRecordsOnlineByCompanyId(profix,companyId,starTime,endTime);
                break;
            default:
                break;
        }
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setContent(callTaskIndexDtos);
        if (callTaskIndexDtos.size() > 0){
            returnMsg.setCode(0);
        }else {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("查无数据！");
        }
        return returnMsg;
    }
}
