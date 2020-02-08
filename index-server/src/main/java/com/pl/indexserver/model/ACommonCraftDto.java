package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pl.model.ACommonCraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ACommonCraftDto extends ACommonCraft {
    //录音文件名称
    private String recordName = "";

    private List<Map<String, String>> recordDetail = new ArrayList<>();//用于接收前端录音详细数据以解析

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public List<Map<String, String>> getRecordDetail() {
        return recordDetail;
    }

    public void setRecordDetail(List<Map<String, String>> recordDetail) {
        this.recordDetail = recordDetail;
    }
}
