package com.pl.model.wx;

/**
 * @author HuangGuangLei
 * @Date 2019/9/28
 */
public class SysLabelDto {
    private Integer id;
    private String name;
    private String level;
    private Integer fathId;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getFathId() {
        return fathId;
    }

    public void setFathId(Integer fathId) {
        this.fathId = fathId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
