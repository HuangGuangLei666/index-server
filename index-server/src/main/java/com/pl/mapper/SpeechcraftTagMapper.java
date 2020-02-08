package com.pl.mapper;

import com.pl.indexserver.model.SpeechcraftTagDto;
import com.pl.model.SpeechcraftTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpeechcraftTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SpeechcraftTag record);

    int insertSelective(SpeechcraftTag record);

    SpeechcraftTag selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpeechcraftTag record);

    int updateByPrimaryKey(SpeechcraftTag record);

    List<SpeechcraftTagDto> selectSpeechcraftTagDtoByCompanyIdAndType(@Param("companyId") Long companyId, @Param("type") String type);

    String getSpeechcraftTag(@Param("companyId") Long companyId,@Param("name")  String name);

    List<SpeechcraftTag> selectByCompanyId(Long companyId);
}