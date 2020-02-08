package com.pl.model;

public class WorkflowLink {
    private Long id;

    private Long fromId;

    private Long nextId;

    private Long workflowId;

    private Long companyId;

    private Long businessId;

    private Long rule_id;

    private Integer priority;

    private Integer byAlgorithm;

    private Integer state;

    private Long responseId;

    public WorkflowLink() {
    }

    public WorkflowLink(Long id, Long fromId, Long nextId, Long workflowId, Long companyId, Long businessId, Long ruleId, Integer priority, Integer byAlgorithm, Integer state, Long responseId) {
        this.id = id;
        this.fromId = fromId;
        this.nextId = nextId;
        this.workflowId = workflowId;
        this.companyId = companyId;
        this.businessId = businessId;
        this.rule_id = ruleId;
        this.priority = priority;
        this.byAlgorithm = byAlgorithm;
        this.state = state;
        this.responseId = responseId;
    }

    public WorkflowLink(Long fromId, Long nextId, Long workflowId, Long companyId, Long businessId, Long ruleId, Integer priority, Integer byAlgorithm, Integer state, Long responseId) {
        this.fromId = fromId;
        this.nextId = nextId;
        this.workflowId = workflowId;
        this.companyId = companyId;
        this.businessId = businessId;
        this.rule_id = ruleId;
        this.priority = priority;
        this.byAlgorithm = byAlgorithm;
        this.state = state;
        this.responseId = responseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getNextId() {
        return nextId;
    }

    public void setNextId(Long nextId) {
        this.nextId = nextId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
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

    public Long getRule_id() {
        return rule_id;
    }

    public void setRule_id(Long rule_id) {
        this.rule_id = rule_id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getByAlgorithm() {
        return byAlgorithm;
    }

    public void setByAlgorithm(Integer byAlgorithm) {
        this.byAlgorithm = byAlgorithm;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }
}