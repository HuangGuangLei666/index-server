package com.pl.model.wx;

public class TGroup {
    private Integer id;

    private String openId;

    private String groupName;

    private String groupMemberphones;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getGroupMemberphones() {
        return groupMemberphones;
    }

    public void setGroupMemberphones(String groupMemberphones) {
        this.groupMemberphones = groupMemberphones == null ? null : groupMemberphones.trim();
    }

}