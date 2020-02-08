package com.pl.model.wx;

/**
 * @author HuangGuangLei
 * @Date 2019/11/12
 */
public class IsAgentResp {
    private String openid;
    private String nickname;
    private Integer identity;
    private String orgNum;

    public IsAgentResp(String openid) {
        this.openid = openid;
    }
    public IsAgentResp() {

    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
    }
}
