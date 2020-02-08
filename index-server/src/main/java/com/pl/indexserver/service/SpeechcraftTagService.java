package com.pl.indexserver.service;

import com.pl.indexserver.model.SpeechcraftTagDto;

import java.util.List;

/**
 * 话术标签相关业务
 *
 * @Author bei.zhang
 * @Date 2018/9/5 15:14
 */
public interface SpeechcraftTagService {

    /**
     * 根据公司、标签类型获取标签列表
     *
     * @param companyId
     * @param Type
     * @return
     */
    List<SpeechcraftTagDto> getSpeechcraftTagDtoList(Long companyId, String Type);

    /**
     * 根据标签名获取值
     * @param companyId
     * @param name
     * @return
     */
    String getSpeechcraftTag(Long companyId, String name);
}
