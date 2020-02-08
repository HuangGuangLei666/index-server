package com.pl.model.wx;

import java.util.Date;

public class TSuggestion {
    private Integer id;

    private String openid;

    private String sugFuntion;

    private String sugCraft;

    private String sugOther;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getSugFuntion() {
        return sugFuntion;
    }

    public void setSugFuntion(String sugFuntion) {
        this.sugFuntion = sugFuntion == null ? null : sugFuntion.trim();
    }

    public String getSugCraft() {
        return sugCraft;
    }

    public void setSugCraft(String sugCraft) {
        this.sugCraft = sugCraft == null ? null : sugCraft.trim();
    }

    public String getSugOther() {
        return sugOther;
    }

    public void setSugOther(String sugOther) {
        this.sugOther = sugOther == null ? null : sugOther.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}