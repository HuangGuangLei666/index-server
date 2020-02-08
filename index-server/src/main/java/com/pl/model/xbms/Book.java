package com.pl.model.xbms;

import java.util.Date;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2020/1/16 14:35
 * @Description
 */
public class Book {
    private String friendName;

    private Date createDate;

    private String phone;

    private Integer userId;

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
