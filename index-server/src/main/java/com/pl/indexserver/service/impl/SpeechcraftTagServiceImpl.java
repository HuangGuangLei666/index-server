package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.SpeechcraftTagDto;
import com.pl.indexserver.service.SpeechcraftTagService;
import com.pl.mapper.SpeechcraftTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 话术标签相关业务实现
 *
 * @Author bei.zhang
 * @Date 2018/9/5 15:54
 */
@Service
public class SpeechcraftTagServiceImpl implements SpeechcraftTagService {

    @Autowired
    private SpeechcraftTagMapper speechcraftTagMapper;


    @Override
    public List<SpeechcraftTagDto> getSpeechcraftTagDtoList(Long companyId, String type) {
        return speechcraftTagMapper.selectSpeechcraftTagDtoByCompanyIdAndType(companyId, type);
    }

    @Override
    public String getSpeechcraftTag(Long companyId, String name) {
        return speechcraftTagMapper.getSpeechcraftTag(companyId, name);
    }
}
