package com.pl.indexserver.model;

import java.io.Serializable;

public class TCallAgentSelectDto implements Serializable {

    private Long id;
    private String agentNum;
    private int agentSatus;
    private String ctName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgentNum() {
        return agentNum;
    }

    public void setAgentNum(String agentNum) {
        this.agentNum = agentNum;
    }

    public int getAgentSatus() {
        return agentSatus;
    }

    public void setAgentSatus(int agentSatus) {
        this.agentSatus = agentSatus;
    }

    public String getCtName() {
        return ctName;
    }

    public void setCtName(String ctName) {
        this.ctName = ctName;
    }


}
