package com.pl.model;

import java.io.Serializable;

public class SysBusinessTemplate implements Serializable {

    private String businessType;

    private String businessTypeName;

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessTypeName() {
        return businessTypeName;
    }

    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }
}
