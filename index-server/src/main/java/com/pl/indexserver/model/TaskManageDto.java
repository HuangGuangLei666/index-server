package com.pl.indexserver.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Collection;
import java.util.List;

public class TaskManageDto {
    //任务标识
    public Long taskId;
    //公司id
    public Long companyId;

    // 1：启动任务,2：增加任务号码;3:暂停任务;
    public Integer operateType;

    //电话号码列表
    public Collection<String> telephoneList;

    public CallTaskAdditionalInfo additionalInfo;

    @Override
    public String toString() {
        return "TaskManageDto{" +
                "taskId=" + taskId +
                "companyId=" + companyId +
                ", operateType=" + operateType +
                ", additionalInfo=" + JSONObject.toJSONString(additionalInfo) +
                ", telephoneList=" + telephoneList +
                '}';
    }
}
