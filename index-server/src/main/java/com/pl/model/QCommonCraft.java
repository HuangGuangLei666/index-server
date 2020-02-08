package com.pl.model;

import com.pl.indexserver.config.LogCompareName;

import java.util.Date;

public class QCommonCraft {

    //主键id
    private Long id;
    //话术标识
    private String craftId;
    @LogCompareName(name = "话术标题")
    private String name = "";
    
    private String interupt = "true";
    
    //业务id
    private Long businessId;
    //公司id
    private Long companyId;
    @LogCompareName(name = "关键字", isKeywordCompare = true)
    private String keyWord = "";
    //关键词数量
    private Integer keyNum = 0;
    //问题类型
    private Integer type;
    //跳转到指定流程id
    private Long jump;
    //回答后处理方式
    private Integer action = 0;

    //短信模板（当且仅当为0时表示不发送短信）
    private Long msgtemplId;
    //状态
    private Integer status;
    //触发类型
    private Integer ruleType;
    //权限级别
    private Integer flag;
    //用户标识
    private String uid;
    //创建时间
    private Date createDate;
    //最近修改时间
    private Date modifyDate;

    @LogCompareName(name = "触发话术", isKeywordCompare = true)
    private String question = "";
    //得分
    private Integer score;

    @LogCompareName(name = "关注点", isKeywordCompare = true)
    private String foucs;

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

    public Long getMsgtemplId() {
        return msgtemplId;
    }

    public void setMsgtemplId(Long msgtemplId) {
        this.msgtemplId = msgtemplId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
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

	public String getInterupt() {
		return interupt;
	}

	public void setInterupt(String interupt) {
		this.interupt = interupt;
	}
    
    
}