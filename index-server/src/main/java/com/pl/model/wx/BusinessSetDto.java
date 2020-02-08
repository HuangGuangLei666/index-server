package com.pl.model.wx;

/**
 * @author HuangGuangLei
 * @Date 2019/9/26
 */
public class BusinessSetDto {
    private Integer businessId;
    private String businessName;
    private Integer contenId;
    private Integer cpId;
    private String cpName;
    private String cpImage;
    private String businessImage;

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Integer getContenId() {
        return contenId;
    }

    public void setContenId(Integer contenId) {
        this.contenId = contenId;
    }

    public Integer getCpId() {
        return cpId;
    }

    public void setCpId(Integer cpId) {
        this.cpId = cpId;
    }

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public String getCpImage() {
        return cpImage;
    }

    public void setCpImage(String cpImage) {
        this.cpImage = cpImage;
    }

    public String getBusinessImage() {
        return businessImage;
    }

    public void setBusinessImage(String businessImage) {
        this.businessImage = businessImage;
    }
}
