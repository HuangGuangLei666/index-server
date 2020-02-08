package com.pl.model.wx;

public class TLabel {
    private Integer id;

    private String name;

    private String level;

    private Integer fathid;

    private String imageurl;

    public TLabel(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public TLabel() {
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level == null ? null : level.trim();
    }

    public Integer getFathid() {
        return fathid;
    }

    public void setFathid(Integer fathid) {
        this.fathid = fathid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl == null ? null : imageurl.trim();
    }
}