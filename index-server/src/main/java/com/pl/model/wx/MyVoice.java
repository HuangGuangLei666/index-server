package com.pl.model.wx;

/**
 * @author HuangGuangLei
 * @Date 2019/11/7
 */
public class MyVoice {
    private Integer voiceId;
    private String voiceName;
    private String voiceImage;

    public MyVoice(Integer voiceId, String voiceName, String voiceImage) {
        this.voiceId = voiceId;
        this.voiceName = voiceName;
        this.voiceImage = voiceImage;
    }
    public MyVoice() {

    }
    public MyVoice(Integer voiceId, String voiceName) {
        this.voiceId = voiceId;
        this.voiceName = voiceName;
    }

    public Integer getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(Integer voiceId) {
        this.voiceId = voiceId;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public String getVoiceImage() {
        return voiceImage;
    }

    public void setVoiceImage(String voiceImage) {
        this.voiceImage = voiceImage;
    }
}
