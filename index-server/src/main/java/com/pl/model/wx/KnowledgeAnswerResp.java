package com.pl.model.wx;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/28 12:18
 * @Description
 */
public class KnowledgeAnswerResp {
    private Long id;
    private String knowledgeId;
    private String answer;

    @Override
    public String toString() {
        return "KnowledgeAnswerResp{" +
                "id=" + id +
                ", knowledgeId='" + knowledgeId + '\'' +
                ", answer='" + answer + '\'' +
                '}';
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
