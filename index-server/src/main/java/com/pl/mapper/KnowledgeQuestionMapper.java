package com.pl.mapper;

import com.pl.indexserver.model.KnowledgeQuestionDto;
import com.pl.model.KnowledgeQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface KnowledgeQuestionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(KnowledgeQuestion record);

    int insertSelective(KnowledgeQuestion record);

    KnowledgeQuestion selectByPrimaryKey(Long id);

    KnowledgeQuestion selectQuestionByKnowledgeId(@Param("knowledgeId") String knowledgeId);

    int updateByPrimaryKeySelective(KnowledgeQuestion record);

    int updateByPrimaryKeyWithBLOBs(KnowledgeQuestion record);

    int updateByPrimaryKey(KnowledgeQuestion record);

    /**
     * 根据公司id和业务id查询相应的问答数据
     *
     * @param params  相关参数
     * @return 返回查询到的结果集
     */
    List<KnowledgeQuestionDto> selectByCompanyIdAndBusinessId(Map<String,Object> params);

    /**
     * 根据主键id查询问答详情数据
     *
     * @param id
     * @return
     */
    KnowledgeQuestionDto selectDetaikByPrimaryKey(Long id);

    /**
     * 根据主键id删除相关知识库问答数据
     *
     * @param id 主键id
     * @return
     */
    int deleteDetailByPrimaryKey(Long id);

    /**
     * 根据公司id 智库id查询问答
     *
     * @param companyId
     * @param businessId
     * @return
     */
    List<KnowledgeQuestion> selectKnowledgeQuestionByCompanyIdAndBusinessId(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    /**
     * 根据公司标识和话术标识获取问答
     * @param companyId 公司标识
     * @param knowledgeId   话术标识
     * @return
     */
    KnowledgeQuestion selectByCompanyIdAndKnowledgeId(@Param("companyId")Long companyId,@Param("knowledgeId")String knowledgeId);

    /**
     * 导出问答话术
     * @param map
     * @param
     * @return
     */
    List<KnowledgeQuestion> qas(Map<String, Object> map);

    List<KnowledgeQuestion> selectQuestionList();
}