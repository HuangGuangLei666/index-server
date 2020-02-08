package com.pl.indexserver.query;

public class TDialogQuery extends BaseQuery {

    private Long taskId;
    private String beginDate;
    private String endDate;
    private String intentionLevel;
    private String status;
    private String type;
    private String telephone;
    private String isIntention;
    private Boolean allIsIntention;
    private String tablePostfix;
    private String tableName;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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

    public String getIntentionLevel() {
        return intentionLevel;
    }

    public void setIntentionLevel(String intentionLevel) {
        this.intentionLevel = intentionLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getIsIntention() {
        return isIntention;
    }

    public void setIsIntention(String isIntention) {
        this.isIntention = isIntention;
    }

    public Boolean getAllIsIntention() {
        return allIsIntention;
    }

    public void setAllIsIntention(Boolean allIsIntention) {
        this.allIsIntention = allIsIntention;
    }

    public String getTablePostfix() {
        return tablePostfix;
    }

    public void setTablePostfix(String tablePostfix) {
        this.tablePostfix = tablePostfix;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
