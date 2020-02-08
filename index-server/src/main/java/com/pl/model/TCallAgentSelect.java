package com.pl.model;

import java.sql.Timestamp;

public class TCallAgentSelect {
    
    private Long id;
    private String agentNum;
    private int dialogStatus;
    private int agentStatus;
    private String ctName;

    private Timestamp beginDate;
    private Timestamp endDate;
    private Timestamp createDate;

    private Long agentId;

    public Long getId() {
        return id;
    }

    public String getAgentNum() {
        return agentNum;
    }

    public int getDialogStatus() {
        return dialogStatus;
    }


    public String getCtName() {
        return ctName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAgentNum(String agentNum) {
        this.agentNum = agentNum;
    }

    public void setDialogStatus(int dialogStatus) {
        this.dialogStatus = dialogStatus;
    }


    public void setCtName(String ctName) {
        this.ctName = ctName;
    }

    public int getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(int agentStatus) {
        this.agentStatus = agentStatus;
    }

    public Timestamp getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Timestamp beginDate) {
        this.beginDate = beginDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
}
