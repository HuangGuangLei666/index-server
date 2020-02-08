package com.pl.indexserver.model;

import com.pl.model.KnowledgeQuestion;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeQuestionDto extends KnowledgeQuestion{

    private String workflowName;

    private String jumpWorkflowName;

    //待录音量
    private Integer flowNum=0;

    private List<KnowledgeAnswerDto> answerList = new ArrayList<>();

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

    public String getJumpWorkflowName() {
        return jumpWorkflowName;
    }

    public void setJumpWorkflowName(String jumpWorkflowName) {
        this.jumpWorkflowName = jumpWorkflowName;
    }

    public List<KnowledgeAnswerDto> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<KnowledgeAnswerDto> answerList) {
        this.answerList = answerList;
    }
}
