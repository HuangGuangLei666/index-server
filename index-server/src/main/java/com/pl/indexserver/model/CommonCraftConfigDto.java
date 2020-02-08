package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 通用话术配置dto
 *
 * @Author bei.zhang
 * @Date 2018/8/8 18:12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonCraftConfigDto {

    /**
     * 主键标识
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 触发话术
     */
    private String question;
    /**
     * 启用状态 0:停用,1:启用
     */
    private Integer enabledStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getEnabledStatus() {
        return enabledStatus;
    }

    public void setEnabledStatus(Integer enabledStatus) {
        this.enabledStatus = enabledStatus;
    }
}
