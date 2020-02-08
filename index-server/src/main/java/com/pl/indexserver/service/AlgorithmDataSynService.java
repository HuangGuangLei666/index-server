package com.pl.indexserver.service;

import com.pl.model.AlgorithmKnowledgeQuestion;
import com.pl.model.AlgorithmQCommonCraft;
import com.pl.model.AlgorithmResponseMode;

/**
 * 算法数据同步业务类
 */
public interface AlgorithmDataSynService {

    /**
     * 同步智库问题
     *
     * @param knowledgeQuestion
     * @return
     */
    int synKnowledgeQuestion(AlgorithmKnowledgeQuestion knowledgeQuestion);


    /**
     * 同步删除智库问题
     *
     * @param companyId
     * @param businessId
     * @param originalId
     * @return
     */
    int synDeleteKnowledgeQuestion(Long companyId, Long businessId, Long originalId);

    /**
     * 同步通用话术
     *
     * @param qCommonCraft
     * @return
     */
    int synQCommonCraft(AlgorithmQCommonCraft qCommonCraft);

    /**
     * 同步删除通用话术
     *
     * @param companyId
     * @param businessId
     * @param originalId
     * @return
     */
    int synDeleteQCommonCraft(Long companyId, Long businessId, Long originalId);

    /**
     * 同步响应方式
     *
     * @param responseMode
     * @return
     */
    int synResponseMode(AlgorithmResponseMode responseMode);

    /**
     * 同步删除响应方式
     *
     * @param companyId
     * @param businessId
     * @param originalId
     * @return
     */
    int synDeleteResponseMode(Long companyId, Long businessId, Long originalId);
}
