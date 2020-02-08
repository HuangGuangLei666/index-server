package com.pl.model.wx;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/10/11
 */
public class DialogDto {
    private Integer id;
    private String calledPhone;
    private String callerPhone;
    private Date beginDate;
    private Date endDate;
    private Integer total_seconds;
    private Date createDate;
    private String simpleWord;
    private String callerPhoneType;
    private String friendName;
    //对话的目的，判断是否走进子流程节点
    private List<String> focus;
    //清单是否已读
    private String isRead;
    //通讯录分类
    private String groupName;

    public List<String> getFocus() {
        return focus;
    }

    public void setFocus(List<String> focus) {
        this.focus = focus;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getCallerPhoneType() {
        return callerPhoneType;
    }

    public void setCallerPhoneType(String callerPhoneType) {
        this.callerPhoneType = callerPhoneType;
    }

    public String getSimpleWord() {
        return simpleWord;
    }

    public void setSimpleWord(String simpleWord) {
        this.simpleWord = simpleWord;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCalledPhone() {
        return calledPhone;
    }

    public void setCalledPhone(String calledPhone) {
        this.calledPhone = calledPhone;
    }

    public String getCallerPhone() {
        return callerPhone;
    }

    public void setCallerPhone(String callerPhone) {
        this.callerPhone = callerPhone;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getTotal_seconds() {
        return total_seconds;
    }

    public void setTotal_seconds(Integer total_seconds) {
        this.total_seconds = total_seconds;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
