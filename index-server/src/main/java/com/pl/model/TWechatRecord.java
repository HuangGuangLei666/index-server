package com.pl.model;

public class TWechatRecord {
    private Long id;
    private Long businessId;
    private String workNodeId;
    private String robotWechatid;
    private String userWechatid;
    private Long bussinessId;
    private String bussinessName;
    private String startSession;
    private String content;
    private String timeStamp;
    private String checkCode;

    public String getStartSession() {
        return startSession;
    }

    public void setStartSession(String startSession) {
        this.startSession = startSession;
    }

    public Long getId() {
        return id;
    }

    public String getWorkNodeId() {
        return workNodeId;
    }

    public void setWorkNodeId(String workNodeId) {
        this.workNodeId = workNodeId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(Long bussinessId) {
        this.bussinessId = bussinessId;
    }

    public String getBussinessName() {
        return bussinessName;
    }

    public void setBussinessName(String bussinessName) {
        this.bussinessName = bussinessName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getRobotWechatid() {
        return robotWechatid;
    }

    public void setRobotWechatid(String robotWechatid) {
        this.robotWechatid = robotWechatid == null ? null : robotWechatid.trim();
    }

    public String getUserWechatid() {
        return userWechatid;
    }

    public void setUserWechatid(String userWechatid) {
        this.userWechatid = userWechatid == null ? null : userWechatid.trim();
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getWorknodeId() {
        return workNodeId;
    }

    public void setWorknodeId(String workNodeId) {
        this.workNodeId = workNodeId;
    }
}