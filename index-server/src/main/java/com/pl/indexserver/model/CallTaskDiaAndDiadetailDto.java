package com.pl.indexserver.model;

/**
 * @author HuangGuangLei
 * @Date 2019/7/12
 */
public class CallTaskDiaAndDiadetailDto {
    private Long taskId;
    private Long CompanyId;
    private Long businessId;
    private String telephone;
    private int priority;
    private Long dialogId;
    private String detailRecords;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(Long companyId) {
        CompanyId = companyId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

    public String getDetailRecords() {
        return detailRecords;
    }

    public void setDetailRecords(String detailRecords) {
        this.detailRecords = detailRecords;
    }
}
