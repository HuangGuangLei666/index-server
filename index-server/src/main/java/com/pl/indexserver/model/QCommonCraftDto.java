package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pl.model.QCommonCraft;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QCommonCraftDto extends QCommonCraft {

    private String workflowName;
    //触发类型
    private String ruleTypeDetail = "";
    //待录音量
    private Integer flowNum = 0;

    private List<ACommonCraftDto> aCommonCraftDtos = new ArrayList<>();


    public Integer getFlowNum() {
        return flowNum;
    }

    public void setFlowNum(Integer flowNum) {
        this.flowNum = flowNum;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }


    public String getRuleTypeDetail() {
        return ruleTypeDetail;
    }

    public void setRuleTypeDetail(String ruleTypeDetail) {
        this.ruleTypeDetail = ruleTypeDetail;
    }

    public List<ACommonCraftDto> getaCommonCraftDtos() {
        return aCommonCraftDtos;
    }

    public void setaCommonCraftDtos(List<ACommonCraftDto> aCommonCraftDtos) {
        this.aCommonCraftDtos = aCommonCraftDtos;
    }

}
