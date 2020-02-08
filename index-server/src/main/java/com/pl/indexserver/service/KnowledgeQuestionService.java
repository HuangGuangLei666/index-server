package com.pl.indexserver.service;

import com.pl.indexserver.model.KnowledgeAnswerDto;
import com.pl.indexserver.model.KnowledgeQuestionDto;
import com.pl.indexserver.model.redisdto.AlgorithmDataSynDto;
import com.pl.model.KnowledgeQuestion;
import com.pl.model.TmUser;

import java.util.List;
import java.util.Map;

/**
 * 问答服务类
 */
public interface KnowledgeQuestionService {

    KnowledgeQuestion selectByPrimaryKey(Long id);

    KnowledgeQuestion selectQuestionByKnowledgeId(String knowledgeId);
    /**
     * 根据公司id和业务id查询对应的结果
     *
     * @param companyId  公司id
     * @param businessId 业务id
     * @return 返回对应的结果集
     */
    List<KnowledgeQuestionDto> selectByCompanyIdAndBusinessId(Long companyId, Long businessId, Long workflowId, String name) throws Exception;

    /**
     * 根据指定主键id查询详情
     *
     * @param id 主键id
     * @return 返回查询到的详细结果
     */
    KnowledgeQuestionDto selectDetailByPrimaryKey(Long id) throws  Exception;


    /**
     * 新增一条问答详情数据
     *
     * @param knowledgeQuestionDto 问答数据对象
     * @return 返回操作结果
     */
    boolean insert(KnowledgeQuestionDto knowledgeQuestionDto) throws Exception;

    /**
     * 修改指定的问答详情数据
     *
     * @param knowledgeQuestionDto 问答数据对象
     * @return 返回操作结果
     */
    boolean update(KnowledgeQuestionDto knowledgeQuestionDto) throws Exception;

    /**
     * 删除指定的问答详情数据
     *
     * @param id 指定问题数据对象id
     * @return 返回操作结果
     */
    boolean deleteKnowledgeDetail(Long id) throws Exception;

    /**
     * 根据主键id查询其对应的录音文件位置以及文件名
     *
     * @param id 主键id
     * @return 返回查询到的结果;filePath:String,fileNames:String[]
     */
    Map<String, Object> selectFileDetailByPrimaryKey(Long id) throws Exception;

    /**
     * 根据知识标识查询相关的答案数据
     *
     * @param knowledgeId 知识标识
     * @return 返回查询到相应结果集
     */
    List<KnowledgeAnswerDto> selectByKnowledgeId(String knowledgeId) throws Exception;

    /**
     * 将传入的数据转换成内部对象并修改数据
     * @param knowledgeAnswerDtos
     * @throws Exception
     */
    Boolean updateByPrimaryKeySelective(List<KnowledgeAnswerDto> knowledgeAnswerDtos) throws Exception;

    /**
     * 根据公司id统计其知识库问答录音文件所占用的空间大小
     *
     * @param companyId 公司id
     * @return  返回统计结果
     * @throws Exception
     */
    long countFileSizeByCompanyId(Long companyId) throws Exception;

    /**
     * 克隆智库下所有问答
     *
     * @param user
     * @param sourceCompanyId
     * @param sourceBusinessId
     * @param targetCompanyId
     * @param targetBusinessId
     * @return
     */
    int clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId,Map<Long, Long> flowIdsMap);

    /**
     * 推送算法同步数据到队列
     *
     * @param operation
     * @param id
     * @param companyId
     * @param businessId
     */
    void pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum operation, Long id, Long companyId, Long businessId);

    List<KnowledgeQuestion> selectQuestionList();
}
