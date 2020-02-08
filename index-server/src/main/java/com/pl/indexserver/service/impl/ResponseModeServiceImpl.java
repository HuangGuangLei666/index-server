package com.pl.indexserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.ResponseModeDto;
import com.pl.indexserver.model.SpeechcraftTagDto;
import com.pl.indexserver.model.redisdto.AlgorithmDataSynDto;
import com.pl.indexserver.service.ResponseModeService;
import com.pl.indexserver.service.SpeechcraftTagService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.RedisClient;
import com.pl.indexserver.untils.TagUtils;
import com.pl.mapper.ResponseModeMapper;
import com.pl.mapper.WorkflowLinkMapper;
import com.pl.model.ResponseMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResponseModeServiceImpl implements ResponseModeService {

    private static final Logger logger = LoggerFactory.getLogger(ResponseModeServiceImpl.class);

    @Autowired
    private ResponseModeMapper responseModeMapper;
    @Autowired
    private WorkflowLinkMapper workflowLinkMapper;
    @Autowired
    private SpeechcraftTagService speechcraftTagService;
    @Value("${redis.algorithmDataSyn}")
    private String algorithmDataSyn;
    @Autowired
    private RedisClient redisClient;

    @Override
    public List<ResponseModeDto> selectByCompanyId(String companuId) throws Exception {
        List<ResponseModeDto> responseModeDtos = responseModeMapper.selectByCompanyId(companuId);
        if (null != responseModeDtos) {
            // 获取当前公司所有话术标签
            List<SpeechcraftTagDto> speechcraftTagDtos = speechcraftTagService.getSpeechcraftTagDtoList(Long.valueOf(companuId), null);
            for (int i = 0; i < responseModeDtos.size(); i++) {
                ResponseModeDto responseModeDto = responseModeDtos.get(i);
                String modifyDate = responseModeDto.getModifyDate();
                if ("".equals(modifyDate) || null == modifyDate) {
                    responseModeDto.setModifyDate(responseModeDto.getCreateDate());
                }
                if (5555 == responseModeDto.getRuleType()) {
                    responseModeDto.setContent(TagUtils.disposeResponseContent(responseModeDto.getContent(), speechcraftTagDtos));
                } else {
                    responseModeDto.setContent(responseModeDto.getContent());
                }
            }
        }
        return responseModeDtos;
    }

    @Override
    public ResponseModeDto selectByPrimaryKey(String id) throws Exception {
        ResponseMode responseMode = responseModeMapper.selectByPrimaryKey(Long.valueOf(id));
        if (null == responseMode) {
            return null;
        }
        ResponseModeDto responseModeDto = new ResponseModeDto();
        responseModeDto.setId(responseMode.getId());
        responseModeDto.setCompanyId(responseMode.getCompanyId());
        responseModeDto.setBusinessId(responseMode.getBusinessId());
        responseModeDto.setName(responseMode.getName());
        responseModeDto.setRuleType(responseMode.getRuleType());
        if (5555 == responseMode.getRuleType()) {
            // 获取当前公司所有话术标签
            List<SpeechcraftTagDto> speechcraftTagDtos = speechcraftTagService.getSpeechcraftTagDtoList(responseMode.getCompanyId(), null);
            responseModeDto.setContent(TagUtils.disposeResponseContent(responseMode.getContent(), speechcraftTagDtos));
        } else {
            responseModeDto.setContent(responseMode.getContent());
        }
        responseModeDto.setKeyWord(responseMode.getKeyWord());
//        responseModeDto.setBusinessId(responseMode.getBusinessId().toString());
        return responseModeDto;
    }

    @Override
    public Boolean selectWorkFlowByPrimaryKey(String id) throws Exception {
        int i = workflowLinkMapper.selectByResponseId(id);
        return i > 0;
    }

    @Override
    public Map<Long,Long> clone(Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId) {
        Map<Long,Long> responseModeId = new HashMap<>();
        Date date = new Date();
        List<ResponseMode> targetResponseModes = responseModeMapper.selectByWorkFlow(sourceCompanyId, sourceBusinessId);
        if (null == targetResponseModes || targetResponseModes.isEmpty()) {
            return responseModeId;
        }
        for (ResponseMode responseMode : targetResponseModes) {
            ResponseMode target = new ResponseMode();
            BeanUtils.copyProperties(responseMode, target, "id", "modifyDate");
            target.setCompanyId(targetCompanyId);
            target.setBusinessId(targetBusinessId);
            target.setCreateDate(date);
            target.setModifyDate(date);
            responseModeMapper.insertSelective(target);
            responseModeId.put(responseMode.getId(),target.getId());
        }
        return responseModeId;
    }

    private List<ResponseMode> selectByWorkFlow(Long companyId, Long businessId) throws Exception{
        return responseModeMapper.selectByWorkFlow(companyId,businessId);
    }

    @Override
    public int deleteByPrimaryKey(String id) throws Exception {
       return responseModeMapper.deleteByPrimaryKey(Long.valueOf(id));
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByPrimaryKeySelective(ResponseModeDto responseModeDto) throws Exception {
    	
    	logger.info(responseModeDto.getId()+" 11 responseModeDto.keyWord=" +responseModeDto.getKeyWord() + "\n responseModeDto.comtent=" + responseModeDto.getContent()  );
        
    	responseModeDto.setKeyWord(KnowledgeQuestionServiceImpl.trimString(responseModeDto.getKeyWord()));  
          
        responseModeDto.setContent(KnowledgeQuestionServiceImpl.trimString(responseModeDto.getContent()));  
          
        logger.info(responseModeDto.getId()+" 22 responseModeDto.keyWord=" +responseModeDto.getKeyWord() + "\n responseModeDto.comtent=" + responseModeDto.getContent()  );
        
          
        ResponseMode responseMode = new ResponseMode();
        responseMode.setId(responseModeDto.getId());
        responseMode.setName(responseModeDto.getName());
        String content = responseModeDto.getContent();
        responseMode.setContent(content==null?"":content);
        responseMode.setUid(responseModeDto.getUid());
        
      
        
        String keyWord = responseModeDto.getKeyWord();
        if ("".equals(keyWord) || null == keyWord || "".equals(keyWord.trim())) {
            responseMode.setKeyWord("");
            responseMode.setKeyNum(0);
        } else {
            int keyNnum = keyWord.split("&").length;
            responseMode.setKeyWord(keyWord);
            responseMode.setKeyNum(keyNnum);
        }
        responseMode.setModifyDate(new Date());
        
        
        return responseModeMapper.updateByPrimaryKeySelective(responseMode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(ResponseModeDto responseModeDto) throws Exception {
        ResponseMode responseMode = new ResponseMode();
        responseMode.setCompanyId(responseModeDto.getCompanyId());
        responseMode.setCreateDate(new Date());
        responseMode.setStatus(CommonConstant.STATUS_0);
        responseMode.setRuleType(CommonConstant.RULETYPE_1);
        responseMode.setFlag(CommonConstant.FLAG_0);
        responseMode.setBusinessId(Long.valueOf(CommonConstant.INITIAL_BUSINESSID));
        responseMode.setUid(responseModeDto.getUid());
        String keyWord = responseModeDto.getKeyWord();
        if ("".equals(keyWord) || null == keyWord || "".equals(keyWord.trim())) {
            responseMode.setKeyWord(null);
            responseMode.setKeyNum(0);
        } else {
            int keyNnum = keyWord.split("&").length;
            responseMode.setKeyWord(keyWord);
            responseMode.setKeyNum(keyNnum);
        }
        responseMode.setName(responseModeDto.getName());
        responseMode.setContent(responseModeDto.getContent());
        int count = responseModeMapper.insert(responseMode);
        if (count > 0) {
            responseModeDto.setId(responseMode.getId());
        }
        return count;
    }

    @Override
    public void pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum operation, Long id, Long companyId, Long businessId) {
        try {
            AlgorithmDataSynDto synDto = new AlgorithmDataSynDto(AlgorithmDataSynDto.TypeEnum.RESPONSE_MODE, operation, id, companyId, businessId);
            redisClient.lpush(algorithmDataSyn, JSONObject.toJSONString(synDto));
        } catch (Exception ex) {
        	logger.error("syn {} response_mode redis push error, id:{},companyId:{},businessId:{}", operation, id, companyId, businessId, ex);
        }
    }
}
