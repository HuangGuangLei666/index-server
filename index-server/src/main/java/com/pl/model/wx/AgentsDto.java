package com.pl.model.wx;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 10:36
 * @Description
 */
public class AgentsDto {
    private String openid;
    private Integer isMembership;
    private Integer identity;
    private String nickName;
    private Integer payCount;
    private Integer actCount;

    public Integer getPayCount() {
        return payCount;
    }

    public void setPayCount(Integer payCount) {
        this.payCount = payCount;
    }

    public Integer getActCount() {
        return actCount;
    }

    public void setActCount(Integer actCount) {
        this.actCount = actCount;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getIsMembership() {
        return isMembership;
    }

    public void setIsMembership(Integer isMembership) {
        this.isMembership = isMembership;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
