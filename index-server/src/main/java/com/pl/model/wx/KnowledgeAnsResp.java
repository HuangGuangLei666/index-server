package com.pl.model.wx;

import com.pl.indexserver.config.LogCompareName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/28 11:19
 * @Description 单个问题下的回答resp
 */
public class KnowledgeAnsResp {
    private Long id;

    private String knowledgeId;

    @LogCompareName(name = "问答名称")
    private String name = "";

    private List<String> answerList = new ArrayList<>();

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

    public List<String> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<String> answerList) {
        this.answerList = answerList;
    }
}
