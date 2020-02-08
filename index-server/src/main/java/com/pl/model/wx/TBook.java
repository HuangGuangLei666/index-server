package com.pl.model.wx;

public class TBook {
    private String phone;

    private String openId;

    private String friendName;

    private String bussnessName;

    public String getBussnessName() {
        return bussnessName;
    }

    public void setBussnessName(String bussnessName) {
        this.bussnessName = bussnessName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName == null ? null : friendName.trim();
    }

}