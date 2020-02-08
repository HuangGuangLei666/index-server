package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.AlgorithmDataSynService;
import com.pl.mapper.AlgorithmKnowledgeQuestionMapper;
import com.pl.mapper.AlgorithmQCommonCraftMapper;
import com.pl.mapper.AlgorithmResponseModeMapper;
import com.pl.model.AlgorithmKnowledgeQuestion;
import com.pl.model.AlgorithmQCommonCraft;
import com.pl.model.AlgorithmResponseMode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AlgorithmDataSynServiceImpl implements AlgorithmDataSynService {

    @Autowired
    private AlgorithmKnowledgeQuestionMapper knowledgeQuestionMapper;
    @Autowired
    private AlgorithmQCommonCraftMapper qCommonCraftMapper;
    @Autowired
    private AlgorithmResponseModeMapper responseModeMapper;


    @Override
    public int synKnowledgeQuestion(AlgorithmKnowledgeQuestion knowledgeQuestion) {
        AlgorithmKnowledgeQuestion origin = knowledgeQuestionMapper.selectByCompanyIdAndBusinessIdAndOriginalId(knowledgeQuestion.getCompanyId(), knowledgeQuestion.getBusinessId(), knowledgeQuestion.getOriginalId());
        knowledgeQuestion.setSynDate(new Date());
        if (origin == null) {
            return knowledgeQuestionMapper.insertSelective(knowledgeQuestion);
        } else {
            BeanUtils.copyProperties(knowledgeQuestion, origin, "id");
            return knowledgeQuestionMapper.updateByPrimaryKeyWithBLOBs(origin);
        }
    }

    @Override
    public int synDeleteKnowledgeQuestion(Long companyId, Long businessId, Long originalId) {
        return knowledgeQuestionMapper.deleteByCompanyIdAndBusinessIdAndOriginalId(companyId, businessId, originalId);
    }


    @Override
    public int synQCommonCraft(AlgorithmQCommonCraft qCommonCraft) {
        AlgorithmQCommonCraft origin = qCommonCraftMapper.selectByCompanyIdAndBusinessIdAndOriginalId(qCommonCraft.getCompanyId(), qCommonCraft.getBusinessId(), qCommonCraft.getOriginalId());
        qCommonCraft.setSynDate(new Date());
        if (origin == null) {
            return qCommonCraftMapper.insertSelective(qCommonCraft);
        } else {
            BeanUtils.copyProperties(qCommonCraft, origin, "id");
            return qCommonCraftMapper.updateByPrimaryKeyWithBLOBs(origin);
        }
    }

    @Override
    public int synDeleteQCommonCraft(Long companyId, Long businessId, Long originalId) {
        return qCommonCraftMapper.deleteByCompanyIdAndBusinessIdAndOriginalId(companyId, businessId, originalId);
    }

    @Override
    public int synResponseMode(AlgorithmResponseMode responseMode) {
        AlgorithmResponseMode origin = responseModeMapper.selectByCompanyIdAndBusinessIdAndOriginalId(responseMode.getCompanyId(), responseMode.getBusinessId(), responseMode.getOriginalId());
        responseMode.setSynDate(new Date());
        if (origin == null) {
            return responseModeMapper.insertSelective(responseMode);
        } else {
            BeanUtils.copyProperties(responseMode, origin, "id");
            return responseModeMapper.updateByPrimaryKeyWithBLOBs(origin);
        }
    }

    @Override
    public int synDeleteResponseMode(Long companyId, Long businessId, Long originalId) {
        return responseModeMapper.deleteByCompanyIdAndBusinessIdAndOriginalId(companyId, businessId, originalId);
    }
}
