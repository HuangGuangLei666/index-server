package com.pl.model;

import java.util.Date;

public class TDialogDetailExt {
    private Long dialogId;

    private Date createDate;

    private String detailRecords;

    private Long taskId;

    private String tablePostfix;

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDetailRecords() {
        return detailRecords;
    }

    public void setDetailRecords(String detailRecords) {
        this.detailRecords = detailRecords == null ? null : detailRecords.trim();
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTablePostfix() {
        return tablePostfix;
    }

    public void setTablePostfix(String tablePostfix) {
        this.tablePostfix = tablePostfix;
    }
}