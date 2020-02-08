package com.pl.model.wx;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 17:22
 * @Description
 */
public class QuestionDto {
    private Long id;
    private String knowledgeId;
    private String name;
    private Long answerId;
    private String ansContent;

    public String getAnsContent() {
        return ansContent;
    }

    public void setAnsContent(String ansContent) {
        this.ansContent = ansContent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }
}
