package com.pl.indexserver.untils;

/**
 * 外呼任务类别枚举值
 *
 * @Author bei.zhang
 * @Date 2018/9/13 11:45
 */
public enum CallTaskCate {

    NORMAL("NORMAL", "普通任务"),
    DINGDING("DINGDING", "鼎鼎任务");

    CallTaskCate(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}