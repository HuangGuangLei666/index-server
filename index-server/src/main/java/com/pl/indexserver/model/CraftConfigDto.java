package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pl.model.SysBusinessTemplate;
import com.pl.model.TBusinessConfig;

import java.util.List;

/**
 * 话术配置dto
 *
 * @Author bei.zhang
 * @Date 2018/8/8 18:12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CraftConfigDto {

    /**
     * 智库标识
     */
    private Long businessId;
    /**
     * 公司标识
     */
    private Long companyId;
    /**
     * 智库名称
     */
    private String name;
    /**
     * 智库描述
     */
    private String remark;

    /* 智库类型 */
    private String templateType;

    /**
     * 未识别状态 1:停用,2:不记次未识别,3:分次未识别
     */
    private Integer unidentifiedStatus;
    /**
     * 无应答状态 1:停用,2:不记次无应答,3:分次无应答
     */
    private Integer unresponsiveStatus;

    private List<SysBusinessTemplate> sysBusinessTemplates;
    /**
     * 通用话术列表
     */
    private List<CommonCraftConfigDto> commonCraftConfigList;

    private BusinessConfigDto focusConfig = new BusinessConfigDto(TBusinessConfig.ConfigType.FOCUS.getCode());
    private BusinessConfigDto scoreConfig = new BusinessConfigDto(TBusinessConfig.ConfigType.SCORE.getCode());
    private BusinessConfigDto intentConfig = new BusinessConfigDto(TBusinessConfig.ConfigType.INTENT.getCode());
    private BusinessConfigDto pushConfig = new BusinessConfigDto(TBusinessConfig.ConfigType.PUSH.getCode());

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getUnidentifiedStatus() {
        return unidentifiedStatus;
    }

    public void setUnidentifiedStatus(Integer unidentifiedStatus) {
        this.unidentifiedStatus = unidentifiedStatus;
    }

    public Integer getUnresponsiveStatus() {
        return unresponsiveStatus;
    }

    public void setUnresponsiveStatus(Integer unresponsiveStatus) {
        this.unresponsiveStatus = unresponsiveStatus;
    }

    public List<CommonCraftConfigDto> getCommonCraftConfigList() {
        return commonCraftConfigList;
    }

    public void setCommonCraftConfigList(List<CommonCraftConfigDto> commonCraftConfigList) {
        this.commonCraftConfigList = commonCraftConfigList;
    }

    public BusinessConfigDto getFocusConfig() {
        return focusConfig;
    }

    public void setFocusConfig(BusinessConfigDto focusConfig) {
        this.focusConfig = focusConfig;
    }

    public BusinessConfigDto getScoreConfig() {
        return scoreConfig;
    }

    public void setScoreConfig(BusinessConfigDto scoreConfig) {
        this.scoreConfig = scoreConfig;
    }

    public BusinessConfigDto getIntentConfig() {
        return intentConfig;
    }

    public void setIntentConfig(BusinessConfigDto intentConfig) {
        this.intentConfig = intentConfig;
    }

    public List<SysBusinessTemplate> getSysBusinessTemplates() {
        return sysBusinessTemplates;
    }

    public void setSysBusinessTemplates(List<SysBusinessTemplate> sysBusinessTemplates) {
        this.sysBusinessTemplates = sysBusinessTemplates;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public BusinessConfigDto getPushConfig() {
        return pushConfig;
    }

    public void setPushConfig(BusinessConfigDto pushConfig) {
        this.pushConfig = pushConfig;
    }
}
