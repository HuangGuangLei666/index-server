package com.pl.model.wx;

import java.util.Date;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/4 10:43
 * @Description
 */
public class BindingOwnUser {
    private String nickName;
    private String phone;
    private String openid;
    private Integer relationId;
    private Date createTime;
    private Integer status;

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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
