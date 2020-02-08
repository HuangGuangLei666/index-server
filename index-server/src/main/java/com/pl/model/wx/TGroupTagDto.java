package com.pl.model.wx;

import java.util.Date;

/**
 * @author HuangGuangLei
 * @Date 2019/11/19
 */
public class TGroupTagDto {

    private String openid;

    private Integer tagId;

    private String tagName;

    private String type;

    private String groupName;

    private String friendName;

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "TGroupTagDto{" +
                ", openid='" + openid + '\'' +
                ", tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                ", type='" + type + '\'' +
                ", groupName='" + groupName + '\'' +
                ", friendName='" + friendName + '\'' +
                '}';
    }
}
