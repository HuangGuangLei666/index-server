package com.pl.indexserver.query;

import org.springframework.util.StringUtils;

public class TCustmIntentionQuery extends BaseQuery {

    private Long taskId;//任务id
    private String taskIds;//由多任务id拼凑的字符串
    private String content;//文本内容(用于未识别)
    private Long businessId;//业务id(用于未识别)
    private String intention;//意向等级（A-E）
    private String beginDate;//通话开始时间
    private String endDate;//通话结束时间
    private String type;// 意向类型
    private String telephone;// 联系号码
    private boolean orderByBeginDateDesc = true;//根据通话开始时间倒序排序

    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int getPageNum() {
        if(!StringUtils.isEmpty(pageSize)){
            return pageSize;
        }else {
            return super.getPageNum();
        }
    }

    public boolean isOrderByBeginDateDesc() {
        return orderByBeginDateDesc;
    }

    public void setOrderByBeginDateDesc(boolean orderByBeginDateDesc) {
        this.orderByBeginDateDesc = orderByBeginDateDesc;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getIntention() {
        return intention;
    }

    public void setIntention(String intention) {
        this.intention = intention;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(String taskIds) {
        this.taskIds = taskIds;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
