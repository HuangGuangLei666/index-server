package com.pl.model;

import com.pl.indexserver.config.LogCompareName;

import java.util.Date;

public class KnowledgeQuestion {

    private Long id;

    private String knowledgeId;

    @LogCompareName(name = "问答名称")
    private String name = "";

    private Long companyId;

    private Long businessId;

    private Long workflowId;
    
    //是否打断  "true" "fasle"
    private String interupt;

    @LogCompareName(name = "关键词", isKeywordCompare = true)
    private String keyWord = "";

    //关键字数量
    private Integer keyNum = 0;

    //问答类型
    private Integer type;

    //跳转到新的流程id
    private Long jump;

    //回答后处理方式
    private Integer action = 0;

    //状态
    private Integer status;

    //用户标识
    private String uid;

    //创建时间
    private Date createDate;
    //修改时间
    private Date modifyDate;

    @LogCompareName(name = "触发话术", isKeywordCompare = true)
    private String question = "";

    //得分
    private Integer score;

    @LogCompareName(name = "关注点", isKeywordCompare = true)
    private String foucs;

    //短信模板标识
    private Long msgtemplId;

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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question == null ? null : question.trim();
    }

    public Long getMsgtemplId() {
        return msgtemplId;
    }

    public void setMsgtemplId(Long msgtemplId) {
        this.msgtemplId = msgtemplId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

	public String getInterupt() {
		return interupt;
	}

	public void setInterupt(String interupt) {
		this.interupt = interupt;
	}
    
    
}