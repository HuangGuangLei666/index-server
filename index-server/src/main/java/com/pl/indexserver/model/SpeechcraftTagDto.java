package com.pl.indexserver.model;

/**
 * 话术标签dto
 *
 * @Author bei.zhang
 * @Date 2018/9/5 16:02
 */
public class SpeechcraftTagDto {

    private Integer id;
    private String tagName;
    private String tagKey;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }
}
