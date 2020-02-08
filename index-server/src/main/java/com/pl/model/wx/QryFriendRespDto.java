package com.pl.model.wx;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/19
 */
public class QryFriendRespDto {
    private String phone;
    private String friendName;
    private long bussnessId;
    private String bussnessName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public long getBussnessId() {
        return bussnessId;
    }

    public void setBussnessId(long bussnessId) {
        this.bussnessId = bussnessId;
    }

    public String getBussnessName() {
        return bussnessName;
    }

    public void setBussnessName(String bussnessName) {
        this.bussnessName = bussnessName;
    }
}
