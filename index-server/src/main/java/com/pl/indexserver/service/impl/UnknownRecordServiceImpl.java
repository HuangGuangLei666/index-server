package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.TUnknownRecordDto;
import com.pl.indexserver.service.UnknownRecordService;
import com.pl.mapper.CallTaskMapper;
import com.pl.mapper.TUnknownRecordMapper;
import com.pl.model.TUnknownRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnknownRecordServiceImpl extends BaseServiceImpl<TUnknownRecord> implements UnknownRecordService {

    @Autowired
    private TUnknownRecordMapper tUnknownRecordMapper;
    @Autowired
    private CallTaskMapper callTaskMapper;

    @Override
    public int neglectByBusinessIdAndContent(Long businessId, String content) throws Exception {
        //查询引用该智库的任务的id
        List<Long> longs = callTaskMapper.selectIdByBusinessId(businessId);
        StringBuilder taskIds = new StringBuilder();
        for (int i = 0; i < longs.size(); i++) {
            Long taskId = longs.get(i);
            if(null!=taskId){
                taskIds.append(taskId+",");
            }
        }
        if (taskIds.length()<0){
            throw  new NullPointerException("查无数据！");
        }
        taskIds.deleteCharAt(taskIds.length()-1);
        return tUnknownRecordMapper.neglectByBusinessIdAndContent(taskIds.toString(),content);
    }
}
