package com.pl.model.xbms;

import java.util.Date;

public class Suggestion {
    private Integer id;

    private Integer userId;

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSugFuntion() {
        return sugFuntion;
    }

    public void setSugFuntion(String sugFuntion) {
        this.sugFuntion = sugFuntion;
    }

    public String getSugCraft() {
        return sugCraft;
    }

    public void setSugCraft(String sugCraft) {
        this.sugCraft = sugCraft;
    }

    public String getSugOther() {
        return sugOther;
    }

    public void setSugOther(String sugOther) {
        this.sugOther = sugOther;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}