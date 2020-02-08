package com.pl.model.wx;

import java.util.Date;

public class TMechanism {
    private Integer id;

    private String name;

    private String openid;

    private String picFacade;

    private String picBack;

    private Integer status;

    private String code;

    private Date createTime;

    private Date adoptTime;

    private String orgNum;

    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
    }

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
        this.name = name == null ? null : name.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPicFacade() {
        return picFacade;
    }

    public void setPicFacade(String picFacade) {
        this.picFacade = picFacade == null ? null : picFacade.trim();
    }

    public String getPicBack() {
        return picBack;
    }

    public void setPicBack(String picBack) {
        this.picBack = picBack == null ? null : picBack.trim();
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
        this.code = code == null ? null : code.trim();
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
}