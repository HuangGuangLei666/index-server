package com.pl.model.xbms;

import java.util.Date;

public class Mechanism {
    private Integer id;

    private String name;

    private Integer userId;

    private String picFacade;

    private String picBack;

    private Integer status;

    private String code;

    private Date createTime;

    private Date adoptTime;

    private String orgNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPicFacade() {
        return picFacade;
    }

    public void setPicFacade(String picFacade) {
        this.picFacade = picFacade;
    }

    public String getPicBack() {
        return picBack;
    }

    public void setPicBack(String picBack) {
        this.picBack = picBack;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getAdoptTime() {
        return adoptTime;
    }

    public void setAdoptTime(Date adoptTime) {
        this.adoptTime = adoptTime;
    }

    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
    }
}