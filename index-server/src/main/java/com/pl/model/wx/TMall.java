package com.pl.model.wx;

public class TMall {
    private Integer id;

    private String name;

    private Integer level;

    private Integer fatherId;

    private String imageUrl;

    private Integer userId;

    public TMall(Integer id, String name, Integer level, Integer fatherId, String imageUrl, Integer userId) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.fatherId = fatherId;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }
    public TMall() {

    }
    public TMall(Integer id,String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getFatherId() {
        return fatherId;
    }

    public void setFatherId(Integer fatherId) {
        this.fatherId = fatherId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? null : imageUrl.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}