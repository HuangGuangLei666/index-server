package com.pl.mapper;

import com.pl.model.AlgorithmKnowledgeQuestion;
import org.apache.ibatis.annotations.Param;

public interface AlgorithmKnowledgeQuestionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AlgorithmKnowledgeQuestion record);

    int insertSelective(AlgorithmKnowledgeQuestion record);

    AlgorithmKnowledgeQuestion selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AlgorithmKnowledgeQuestion record);

    int updateByPrimaryKeyWithBLOBs(AlgorithmKnowledgeQuestion record);

    int updateByPrimaryKey(AlgorithmKnowledgeQuestion record);

    AlgorithmKnowledgeQuestion selectByCompanyIdAndBusinessIdAndOriginalId(@Param("companyId") Long companyId,
                                                                           @Param("businessId") Long businessId,
                                                                           @Param("originalId") Long originalId);

    int deleteByCompanyIdAndBusinessIdAndOriginalId(@Param("companyId") Long companyId,
                                                @Param("businessId") Long businessId,
                                                @Param("originalId") Long originalId);
}