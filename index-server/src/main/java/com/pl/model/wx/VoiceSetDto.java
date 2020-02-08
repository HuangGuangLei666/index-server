package com.pl.model.wx;

/**
 * @author HuangGuangLei
 * @Date 2019/9/26
 */
public class VoiceSetDto {
    private Integer id;
    private Integer businessId;
    private String voiceName;
    private String voice_image;
    private String vpName;

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
        this.voiceName = voiceName;
    }

    public String getVoice_image() {
        return voice_image;
    }

    public void setVoice_image(String voice_image) {
        this.voice_image = voice_image;
    }

    public String getVpName() {
        return vpName;
    }

    public void setVpName(String vpName) {
        this.vpName = vpName;
    }
}
