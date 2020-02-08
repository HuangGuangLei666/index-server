package com.pl.model;

import com.pl.indexserver.config.LogCompareName;

import java.util.Date;

public class Speechcraft {
    private Long id;

    private String craftId;

    @LogCompareName(name = "话术标题")
    private String name;

    private Long businessId;

    private Long companyId;

    @LogCompareName(name = "回答内容")
    private String content;

    private String recordFile;

    private Integer isRecord;

    private Long msgtemplId;

    private Long fileSize;

    private Date createDate;

    private Date modifyDate;

    private Integer score;

    @LogCompareName(name = "关注点", isKeywordCompare = true)
    private String foucs;

    private String recordDescribe;//录音文件描述

    private Integer recordState;//录音文件状态

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

    public Integer getRecordState() {
        return recordState;
    }

    public void setRecordState(Integer recordState) {
        this.recordState = recordState;
    }

    public String getRecordDescribe() {
        return recordDescribe;
    }

    public void setRecordDescribe(String recordDescribe) {
        this.recordDescribe = recordDescribe;
    }

    public String getFoucs() {
        return foucs;
    }

    public void setFoucs(String foucs) {
        this.foucs = foucs;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public Long getMsgtemplId() {
        return msgtemplId;
    }

    public void setMsgtemplId(Long msgtemplId) {
        this.msgtemplId = msgtemplId;
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
}