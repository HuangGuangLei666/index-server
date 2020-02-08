package com.pl.model.xbms;

import java.util.Date;

public class Relation {
    private Integer id;

    private Integer userId;

    private Integer userIdRelation;

    private Date createTime;

    private Integer status;

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

    public Integer getUserIdRelation() {
        return userIdRelation;
    }

    public void setUserIdRelation(Integer userIdRelation) {
        this.userIdRelation = userIdRelation;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}