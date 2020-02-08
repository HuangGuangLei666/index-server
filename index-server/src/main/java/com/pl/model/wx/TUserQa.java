package com.pl.model.wx;

public class TUserQa {
    private Integer id;

    private String openid;

    private String knowledgeId;

    private Long answerId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId == null ? null : knowledgeId.trim();
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    @Override
    public String toString() {
        return "TUserQa{" +
                "id=" + id +
                ", openid='" + openid + '\'' +
                ", knowledgeId='" + knowledgeId + '\'' +
                ", answerId=" + answerId +
                '}';
    }
}