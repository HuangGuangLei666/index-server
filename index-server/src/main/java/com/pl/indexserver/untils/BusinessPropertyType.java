package com.pl.indexserver.untils;

/**
 * 智库属性枚举值
 *
 * @Author bei.zhang
 * @Date 2018/9/13 11:45
 */
public enum BusinessPropertyType {

    BUSINESS_TYPE("BUSINESS_TYPE", "智库类型"),
    BUSINESS_TTS("BUSINESS_TTS", "智库TTS"),
    BUSINESS_AUTODIALER("BUSINESS_AUTODIALER", "自动外呼"),
    BUSINESS_EXPORT_TAG("BUSINESS_EXPORT_TAG", "智库类型"),;

    BusinessPropertyType(String code, String name) {
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