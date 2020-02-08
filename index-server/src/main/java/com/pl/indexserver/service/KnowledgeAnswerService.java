package com.pl.indexserver.service;

import com.pl.indexserver.model.KnowledgeAnswerDto;
import com.pl.model.KnowledgeAnswer;
import com.pl.model.TmUser;

import java.util.List;

/**
 * 问答服务类
 */
public interface KnowledgeAnswerService {

    KnowledgeAnswer selectByPrimaryKey(Long id) throws  Exception;

    /**
     * 新增一条问答详情数据
     *
     * @param knowledgeAnswerDto 回答数据对象
     * @return 返回操作结果
     */
    boolean insertAndRecardLog(KnowledgeAnswerDto knowledgeAnswerDto, TmUser user) throws Exception;

    /**
     * 修改指定的问答详情数据
     *
     * @param knowledgeAnswerDto 回答数据对象
     * @return 返回操作结果
     */
    boolean updateAndRecardLogByPrimaryKey(KnowledgeAnswerDto knowledgeAnswerDto, TmUser user) throws Exception;

    /**
     * 删除指定的回答数据
     *
     * @param id 指定回答数据对象id
     * @return 返回操作结果
     */
    boolean delete(Long id) throws Exception;

    List<KnowledgeAnswer> selectByKnowledgeId(String knowledgeId);

    KnowledgeAnswer selectByAnswerIdAndKnowledgeId(Long answerId, String knowledgeId);
}
