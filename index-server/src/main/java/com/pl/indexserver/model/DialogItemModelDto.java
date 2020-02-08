package com.pl.indexserver.model;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class DialogItemModelDto implements Serializable {

    private String id;//主键

    private String file_path;//录音文件位置

    private String content;//通话内容

    private Integer participant;//通话方

    private String cycleId;//轮标识

    private String workNodeName; // 节点名称

    private String speachCraftName; // 响应方式

    private Object contentDetail;

    public Object getContentDetail() {
        return contentDetail;
    }

    public void setContentDetail(Object contentDetail) {
        if (StringUtils.isEmpty(contentDetail)){
            contentDetail = new ArrayList<>();
        }
        this.contentDetail = contentDetail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCycleId() {
        return cycleId;
    }

    public void setCycleId(String cycleId) {
        this.cycleId = cycleId;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getParticipant() {
        return participant;
    }

    public void setParticipant(Integer participant) {
        this.participant = participant;
    }

    public String getWorkNodeName() {
        return workNodeName;
    }

    public void setWorkNodeName(String workNodeName) {
        this.workNodeName = workNodeName;
    }

    public String getSpeachCraftName() {
        return speachCraftName;
    }

    public void setSpeachCraftName(String speachCraftName) {
        this.speachCraftName = speachCraftName;
    }
}
