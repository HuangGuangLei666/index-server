package com.pl.model;

import org.springframework.format.annotation.DateTimeFormat;

public class SpeechcraftTalkModel {

    private Long speechcraId;//话术ID
    private String talkTitle;//专业话术标题；
    private String processNodeName;
    @DateTimeFormat
    private String createDate;//创建时间；
    @DateTimeFormat
    private String modifyDate;//更新时间；


    public Long getSpeechcraId() {
        return speechcraId;
    }

    public void setSpeechcraId(Long speechcraId) {
        this.speechcraId = speechcraId;
    }

    public String getTalkTitle() {
        return talkTitle;
    }

    public void setTalkTitle(String talkTitle) {
        this.talkTitle = talkTitle;
    }

    public String getProcessNodeName() {
        return processNodeName;
    }

    public void setProcessNodeName(String processNodeName) {
        this.processNodeName = processNodeName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }
}
