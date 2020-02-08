package com.pl.model.wx;

import java.util.Date;

public class TRelation {
    private Integer id;

    private String openid;

    private String openidRelation;

    private Date createTime;

    private Integer status;

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

    public String getOpenidRelation() {
        return openidRelation;
    }

    public void setOpenidRelation(String openidRelation) {
        this.openidRelation = openidRelation == null ? null : openidRelation.trim();
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