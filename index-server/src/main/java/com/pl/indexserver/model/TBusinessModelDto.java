package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TBusinessModelDto {

    private Long id;//主键
    private String name = "";//业务名称
    private String status = "";//状态
    private String remark = "";//备注
    private Long companyId;//公司标识
    private String modifyDate = "";//修改时间
    private String createDate = "";//创建时间
    private Long speechcraftNum = 0L;//话术量
    private Long recordNum = 0L;//已录音量

    private CraftConfigDto craftConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Long getSpeechcraftNum() {
        return speechcraftNum;
    }

    public void setSpeechcraftNum(Long speechcraftNum) {
        this.speechcraftNum = speechcraftNum;
    }

    public Long getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(Long recordNum) {
        this.recordNum = recordNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public CraftConfigDto getCraftConfig() {
        return craftConfig;
    }

    public void setCraftConfig(CraftConfigDto craftConfig) {
        this.craftConfig = craftConfig;
    }
}