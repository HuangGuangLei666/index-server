package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DialogModelDto {

    private String status;//状态
    private int dialogNum;//通话轮次
    private String recordPath;//录音文件地址
    private String companyId;//公司id
    private String taskId;//任务id
    private String taskName;//任务名称
    private String telephone;//电话号码
    private int totalSeconds;//通话时间
    private String intentionLevel;//意向等级
    private String focusLevel;//关注点等级
    private String intentLevel;//多条件意向等级
    private String createDate = "";//创建时间
    private Date beginDate;//外呼时间
    private String ctname = "";//客户名称
    private String phoneType;//号码标记类型
    private String friendName;//好友姓名
    private String groupName;//通讯录分类

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    private List<DialogItemModelDto> temp = new ArrayList<>();

    private List<List<DialogItemModelDto>> contents = new ArrayList<>();

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDialogNum() {
        return dialogNum;
    }

    public void setDialogNum(int dialogNum) {
        this.dialogNum = dialogNum;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public List<DialogItemModelDto> getTemp() {
        return temp;
    }

    public void setTemp(List<DialogItemModelDto> temp) {
        this.temp = temp;
    }

    public List<List<DialogItemModelDto>> getContents() {
        return contents;
    }

    public void setContents(List<List<DialogItemModelDto>> contents) {
        this.contents = contents;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public String getIntentionLevel() {
        return intentionLevel;
    }

    public void setIntentionLevel(String intentionLevel) {
        this.intentionLevel = intentionLevel;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCtname() {
        return ctname;
    }

    public void setCtname(String ctname) {
        this.ctname = ctname;
    }

    public String getFocusLevel() {
        return focusLevel;
    }

    public void setFocusLevel(String focusLevel) {
        this.focusLevel = focusLevel;
    }

    public String getIntentLevel() {
        return intentLevel;
    }

    public void setIntentLevel(String intentLevel) {
        this.intentLevel = intentLevel;
    }

    public void setStatusDetail() {
        if ("0".equals(this.status)) {
            setStatus("未拨打");
        } else if ("1".equals(this.status)) {
            setStatus("呼叫中");
        } else if ("2".equals(this.status)) {
            setStatus("已接通");
        } else if ("13".equals(this.status)) {
            setStatus("空号");
        } else if ("15".equals(this.status)) {
            setStatus("占线");
        } else if ("17".equals(this.status)) {
            setStatus("未接");
        } else if ("18".equals(this.status)) {
            setStatus("主叫失败");
        } else {
            setStatus("");
        }
    }
}
