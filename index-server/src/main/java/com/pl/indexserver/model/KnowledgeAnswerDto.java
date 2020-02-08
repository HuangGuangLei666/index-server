package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pl.model.KnowledgeAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KnowledgeAnswerDto extends KnowledgeAnswer {

    private String craftId;

    private List<Map<String, String>> recordDetail = new ArrayList<>();

    public List<Map<String, String>> getRecordDetail() {
        return recordDetail;
    }

    public void setRecordDetail(List<Map<String, String>> recordDetail) {
        this.recordDetail = recordDetail;
    }

    public String getCraftId() {
        return craftId;
    }

    public void setCraftId(String craftId) {
        this.craftId = craftId;
    }

}
