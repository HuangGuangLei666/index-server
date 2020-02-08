package com.pl.model;

import java.util.Date;

/**
 * @author HuangGuangLei
 * @Date 2019/11/11
 */
public class UserIsMembership {
    private Integer userId;
    private String nickname;
    private String openid;
    private Integer isMembership;
    private Date expireTime;

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public UserIsMembership(String nickname) {
        this.nickname = nickname;
    }
    public UserIsMembership() {

    }
}
