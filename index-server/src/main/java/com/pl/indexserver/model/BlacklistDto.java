package com.pl.indexserver.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author HGL
 * @Date 2018/12/28
 */
public class BlacklistDto implements Serializable {

    private Long id;
    private String ctId;
    private String ctName;
    private Long companyId;
    private Date modifyDate;
    private String ctPhone;
    private String ctQq;
    private String ctWechat;
    private String ctEmail;

    public BlacklistDto(String ctPhone) {
        this.ctPhone = ctPhone;
    }

    public BlacklistDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCtId() {
        return ctId;
    }

    public void setCtId(String ctId) {
        this.ctId = ctId;
    }

    public String getCtName() {
        return ctName;
    }

    public void setCtName(String ctName) {
        this.ctName = ctName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getCtPhone() {
        return ctPhone;
    }

    public void setCtPhone(String ctPhone) {
        this.ctPhone = ctPhone;
    }

    public String getCtQq() {
        return ctQq;
    }

    public void setCtQq(String ctQq) {
        this.ctQq = ctQq;
    }

    public String getCtWechat() {
        return ctWechat;
    }

    public void setCtWechat(String ctWechat) {
        this.ctWechat = ctWechat;
    }

    public String getCtEmail() {
        return ctEmail;
    }

    public void setCtEmail(String ctEmail) {
        this.ctEmail = ctEmail;
    }
}
