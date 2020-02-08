package com.pl.model;

import java.io.Serializable;
import java.util.Date;

public class AlgorithmKnowledgeQuestion implements Serializable {
    private Long id;

    private String knowledgeId;

    private String name;

    private Long companyId;

    private Long businessId;

    private Long originalId;

    private Long workflowId;

    private String keyWord;

    private Integer keyNum;

    private Integer type;

    private Long jump;

    private Integer action;

    private Integer status;

    private String uid;

    private Date createDate;

    private Date modifyDate;

    private Integer score;

    private Long msgtemplId;

    private String foucs;

    private Integer vadEos;

    private Date synDate;

    private String question;

    private static final long serialVersionUID = 1L;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public Long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord == null ? null : keyWord.trim();
    }

    public Integer getKeyNum() {
        return keyNum;
    }

    public void setKeyNum(Integer keyNum) {
        this.keyNum = keyNum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getJump() {
        return jump;
    }

    public void setJump(Long jump) {
        this.jump = jump;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getMsgtemplId() {
        return msgtemplId;
    }

    public void setMsgtemplId(Long msgtemplId) {
        this.msgtemplId = msgtemplId;
    }

    public String getFoucs() {
        return foucs;
    }

    public void setFoucs(String foucs) {
        this.foucs = foucs == null ? null : foucs.trim();
    }

    public Integer getVadEos() {
        return vadEos;
    }

    public void setVadEos(Integer vadEos) {
        this.vadEos = vadEos;
    }

    public Date getSynDate() {
        return synDate;
    }

    public void setSynDate(Date synDate) {
        this.synDate = synDate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question == null ? null : question.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", knowledgeId=").append(knowledgeId);
        sb.append(", name=").append(name);
        sb.append(", companyId=").append(companyId);
        sb.append(", businessId=").append(businessId);
        sb.append(", originalId=").append(originalId);
        sb.append(", workflowId=").append(workflowId);
        sb.append(", keyWord=").append(keyWord);
        sb.append(", keyNum=").append(keyNum);
        sb.append(", type=").append(type);
        sb.append(", jump=").append(jump);
        sb.append(", action=").append(action);
        sb.append(", status=").append(status);
        sb.append(", uid=").append(uid);
        sb.append(", createDate=").append(createDate);
        sb.append(", modifyDate=").append(modifyDate);
        sb.append(", score=").append(score);
        sb.append(", msgtemplId=").append(msgtemplId);
        sb.append(", foucs=").append(foucs);
        sb.append(", vadEos=").append(vadEos);
        sb.append(", synDate=").append(synDate);
        sb.append(", question=").append(question);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}