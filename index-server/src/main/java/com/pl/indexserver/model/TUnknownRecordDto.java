package com.pl.indexserver.model;

import com.pl.model.TUnknownRecord;

import java.util.ArrayList;
import java.util.List;

public class TUnknownRecordDto extends TUnknownRecord {

    private int frequency;//频率
    private Long businessId;//知识库id
    private String businessName;//知识库名称
    private List<TCustmIntentionDto> custmIntentions = new ArrayList<>();//用于存储相关通话信息

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public List<TCustmIntentionDto> getCustmIntentions() {
        return custmIntentions;
    }

    public void setCustmIntentions(List<TCustmIntentionDto> custmIntentions) {
        this.custmIntentions = custmIntentions;
    }
}
