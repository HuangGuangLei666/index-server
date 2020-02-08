package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pl.model.Speechcraft;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpeechcraftModelDto extends Speechcraft {
    private String recordName = "";//录音文件名称
    //    private String foucsNum;//关注点数量
    private List<SpeechcraftModelDto> speechcrafts = new ArrayList<>();//用于转换数据结构以展示

    private List<Map<String, String>> recordDetail = new ArrayList<>();//用于接收前端录音详细数据以解析


    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }


    public List<SpeechcraftModelDto> getSpeechcrafts() {
        return speechcrafts;
    }

    public void setSpeechcrafts(List<SpeechcraftModelDto> speechcrafts) {
        this.speechcrafts = speechcrafts;
    }

    public List<Map<String, String>> getRecordDetail() {
        return recordDetail;
    }

    public void setRecordDetail(List<Map<String, String>> recordDetail) {
        this.recordDetail = recordDetail;
    }

    @Override
    public String getRecordFile() {
        if (StringUtils.isEmpty(super.getRecordFile())) {
            return getRecordName();
        } else {
            return super.getRecordFile();
        }
    }
}
