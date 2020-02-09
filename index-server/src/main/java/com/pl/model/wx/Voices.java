package com.pl.model.wx;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/30 14:32
 * @Description
 */
public class Voices {
    private Integer id;
    private String name;
    private String imageUrl;

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
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Voices(String name) {
        this.name = name;
    }
    public Voices() {
    }
}
