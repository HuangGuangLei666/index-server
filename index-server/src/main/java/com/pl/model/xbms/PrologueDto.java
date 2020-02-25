package com.pl.model.xbms;

import java.util.Date;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2020/2/22 12:15
 * @Description 用户自定义开场白类型
 */
public class PrologueDto {
    private Integer id;
    //开场白类型，1tts合成，2自己录音
    private String type;
    private Date createTime;
    private String content;
    private String recordingUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }
}
