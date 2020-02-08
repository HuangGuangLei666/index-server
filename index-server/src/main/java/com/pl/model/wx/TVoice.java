package com.pl.model.wx;

import java.util.Date;

public class TVoice {
    private Integer id;

    private Integer businessId;

    private String voiceName;

    private Integer vpId;

    private String vpName;

    private Date createTime;

    private String voicePath;

    private String voiceImage;

    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName == null ? null : voiceName.trim();
    }

    public Integer getVpId() {
        return vpId;
    }

    public void setVpId(Integer vpId) {
        this.vpId = vpId;
    }

    public String getVpName() {
        return vpName;
    }

    public void setVpName(String vpName) {
        this.vpName = vpName == null ? null : vpName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath == null ? null : voicePath.trim();
    }

    public String getVoiceImage() {
        return voiceImage;
    }

    public void setVoiceImage(String voiceImage) {
        this.voiceImage = voiceImage == null ? null : voiceImage.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}