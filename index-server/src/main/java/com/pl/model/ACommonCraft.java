package com.pl.model;

import com.pl.indexserver.config.LogCompareName;

import java.io.Serializable;
import java.util.Date;

public class ACommonCraft implements Serializable {
    //主键id
    private Long id;

    private String craftId;
    //业务标识
    private Long businessId;

    //公司标识
    private Long companyId;

    @LogCompareName(name = "回答内容")
    private String content = "";

    //录音文件名称
    private String recordFile;

    private Integer isRecord;

    private Long fileSize;

    private Integer status;

    private String uid;

    private Date createDate;

    private Date modifyDate;
    //得分
    private Integer score;
    //录音状态
    private Integer recordState;

    private String recordDescribe;//录音描述

    private Integer notRecordingNum;

    private String fileSizeCount;

    public String getFileSizeCount() {
        return fileSizeCount;
    }

    public void setFileSizeCount(String fileSizeCount) {
        this.fileSizeCount = fileSizeCount;
    }

    public Integer getNotRecordingNum() {
        return notRecordingNum;
    }

    public void setNotRecordingNum(Integer notRecordingNum) {
        this.notRecordingNum = notRecordingNum;
    }

    public String getRecordDescribe() {
        return recordDescribe;
    }

    public void setRecordDescribe(String recordDescribe) {
        this.recordDescribe = recordDescribe;
    }

    public Integer getRecordState() {
        return recordState;
    }

    public void setRecordState(Integer recordState) {
        this.recordState = recordState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCraftId() {
        return craftId;
    }

    public void setCraftId(String craftId) {
        this.craftId = craftId == null ? null : craftId.trim();
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getRecordFile() {
        return recordFile;
    }

    public void setRecordFile(String recordFile) {
        this.recordFile = recordFile == null ? null : recordFile.trim();
    }

    public Integer getIsRecord() {
        return isRecord;
    }

    public void setIsRecord(Integer isRecord) {
        this.isRecord = isRecord;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}