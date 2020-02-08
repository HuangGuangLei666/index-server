package com.pl.model;

import com.pl.indexserver.config.LogCompareName;

import java.math.BigDecimal;
import java.util.Date;

public class KnowledgeAnswer {

    //主键id
    private Long id;
    //知识标识
    private String knowledgeId = "";
    //公司id
    private Long companyId;
    //智库id
    private Long businessId;
    //录音文件名
    private String recordFile = "";

    //是否录音
    private Integer isRecord;

    private Long fileSize;

    private String label1;

    private String label2;
    //得分
    private Integer score;

    //状态
    private Integer status;

    //用户标识
    private String uid;

    //创建时间
    private Date createDate;

    //修改时间
    private Date modifyDate;

    @LogCompareName(name = "回答内容")
    private String answer = "";

    //短信模板
    private Long msgtemplId;

    private Integer action;

    //录音描述信息
    private String recordDescribe;

    //录音状态
    private Integer recordState;

    private Integer notRecordingNum;

    private String fileSizeCount;

    @LogCompareName(name = "简答内容")
    private String simpleAnswer = "";

    //录音文件名(简版)
    private String recordFileSimple = "";

    public String getSimpleAnswer() {
        return simpleAnswer;
    }

    public void setSimpleAnswer(String simpleAnswer) {
        this.simpleAnswer = simpleAnswer;
    }

    public String getRecordFileSimple() {
        return recordFileSimple;
    }

    public void setRecordFileSimple(String recordFileSimple) {
        this.recordFileSimple = recordFileSimple;
    }

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

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Long getMsgtemplId() {
        return msgtemplId;
    }

    public void setMsgtemplId(Long msgtemplId) {
        this.msgtemplId = msgtemplId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId == null ? null : knowledgeId.trim();
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
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

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1 == null ? null : label1.trim();
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2 == null ? null : label2.trim();
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer == null ? null : answer.trim();
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}