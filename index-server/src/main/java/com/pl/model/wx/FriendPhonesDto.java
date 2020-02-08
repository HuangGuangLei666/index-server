package com.pl.model.wx;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/6 14:49
 * @Description 查询好友下的多个号码
 */
public class FriendPhonesDto {
    private Integer id;
    private String friendName;
    private String phone;
    private String phones;
    private String tagName;
    private String openid;

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
