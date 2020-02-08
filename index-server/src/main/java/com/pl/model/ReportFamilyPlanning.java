package com.pl.model;

import java.io.Serializable;
import java.util.Date;

public class ReportFamilyPlanning implements Serializable {
    private Integer id;

    private String countyCode;

    private String countyName;

    private Long dialogId;

    private String phone;

    private String name;

    private String sex;

    private String age;

    private Byte maintenanceFee;

    private Byte publicService;

    private Byte terminalPregnancy;

    private Byte rewardHelp;

    private Byte twoChildPolicy;

    private String status;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    private String remark;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode == null ? null : countyCode.trim();
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName == null ? null : countyName.trim();
    }

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age == null ? null : age.trim();
    }

    public Byte getMaintenanceFee() {
        return maintenanceFee;
    }

    public void setMaintenanceFee(Byte maintenanceFee) {
        this.maintenanceFee = maintenanceFee;
    }

    public Byte getPublicService() {
        return publicService;
    }

    public void setPublicService(Byte publicService) {
        this.publicService = publicService;
    }

    public Byte getTerminalPregnancy() {
        return terminalPregnancy;
    }

    public void setTerminalPregnancy(Byte terminalPregnancy) {
        this.terminalPregnancy = terminalPregnancy;
    }

    public Byte getRewardHelp() {
        return rewardHelp;
    }

    public void setRewardHelp(Byte rewardHelp) {
        this.rewardHelp = rewardHelp;
    }

    public Byte getTwoChildPolicy() {
        return twoChildPolicy;
    }

    public void setTwoChildPolicy(Byte twoChildPolicy) {
        this.twoChildPolicy = twoChildPolicy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", countyCode=").append(countyCode);
        sb.append(", countyName=").append(countyName);
        sb.append(", dialogId=").append(dialogId);
        sb.append(", phone=").append(phone);
        sb.append(", name=").append(name);
        sb.append(", sex=").append(sex);
        sb.append(", age=").append(age);
        sb.append(", maintenanceFee=").append(maintenanceFee);
        sb.append(", publicService=").append(publicService);
        sb.append(", terminalPregnancy=").append(terminalPregnancy);
        sb.append(", rewardHelp=").append(rewardHelp);
        sb.append(", twoChildPolicy=").append(twoChildPolicy);
        sb.append(", status=").append(status);
        sb.append(", createBy=").append(createBy);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateBy=").append(updateBy);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}