package com.pl.model.wx;

import java.util.Date;

public class TUserinfo {
    private Integer id;

    private String subscribe;

    private String openid;

    private String unionid;
    
    private String sex;

    private String nickname;

    private String language;

    private String city;

    private String province;

    private String country;

    private String headimgurl;

    private Date subscribeTime;

    private String remark;

    private Integer groupid;

    private String tagidList;

    private String subscribeScene;

    private String qrScene;

    private String qrSceneStr;

    private String phonenumber;

    private Date createTime;

    private Integer status;

    private Integer recommenderId;

    private String sonId;

    private Integer voiceId;

    private Integer isMembership;

    private String empNum;

    private Integer identity;

    private Date expireTime;

    private String sosPhone;

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getSosPhone() {
        return sosPhone;
    }

    public void setSosPhone(String sosPhone) {
        this.sosPhone = sosPhone;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public TUserinfo(Integer id, String subscribe, String openid, String sex, String nickname, String language, String city, String province, String country, String headimgurl, Date subscribeTime, String remark, Integer groupid, String tagidList, String subscribeScene, String qrScene, String qrSceneStr, String phonenumber, Date createTime, Integer status, Integer recommenderId, String sonId, Integer voiceId, Integer isMembership, String empNum, Integer identity) {
        this.id = id;
        this.subscribe = subscribe;
        this.openid = openid;
        this.sex = sex;
        this.nickname = nickname;
        this.language = language;
        this.city = city;
        this.province = province;
        this.country = country;
        this.headimgurl = headimgurl;
        this.subscribeTime = subscribeTime;
        this.remark = remark;
        this.groupid = groupid;
        this.tagidList = tagidList;
        this.subscribeScene = subscribeScene;
        this.qrScene = qrScene;
        this.qrSceneStr = qrSceneStr;
        this.phonenumber = phonenumber;
        this.createTime = createTime;
        this.status = status;
        this.recommenderId = recommenderId;
        this.sonId = sonId;
        this.voiceId = voiceId;
        this.isMembership = isMembership;
        this.empNum = empNum;
        this.identity = identity;
    }

    public TUserinfo(String subscribe) {
        this.subscribe = subscribe;
    }
    public TUserinfo() {

    }

    public String getEmpNum() {
        return empNum;
    }

    public void setEmpNum(String empNum) {
        this.empNum = empNum;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public Integer getIsMembership() {
        return isMembership;
    }

    public void setIsMembership(Integer isMembership) {
        this.isMembership = isMembership;
    }

    public Integer getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(Integer voiceId) {
        this.voiceId = voiceId;
    }

    public String getSonId() {
        return sonId;
    }

    public void setSonId(String sonId) {
        this.sonId = sonId;
    }

    public Integer getRecommenderId() {
        return recommenderId;
    }

    public void setRecommenderId(Integer recommenderId) {
        this.recommenderId = recommenderId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe == null ? null : subscribe.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language == null ? null : language.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl == null ? null : headimgurl.trim();
    }

    public Date getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Date subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public String getTagidList() {
        return tagidList;
    }

    public void setTagidList(String tagidList) {
        this.tagidList = tagidList == null ? null : tagidList.trim();
    }

    public String getSubscribeScene() {
        return subscribeScene;
    }

    public void setSubscribeScene(String subscribeScene) {
        this.subscribeScene = subscribeScene == null ? null : subscribeScene.trim();
    }

    public String getQrScene() {
        return qrScene;
    }

    public void setQrScene(String qrScene) {
        this.qrScene = qrScene == null ? null : qrScene.trim();
    }

    public String getQrSceneStr() {
        return qrSceneStr;
    }

    public void setQrSceneStr(String qrSceneStr) {
        this.qrSceneStr = qrSceneStr == null ? null : qrSceneStr.trim();
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber == null ? null : phonenumber.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
    
    
}