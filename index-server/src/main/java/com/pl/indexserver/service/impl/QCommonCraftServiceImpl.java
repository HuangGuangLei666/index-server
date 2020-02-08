package com.pl.indexserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.*;
import com.pl.indexserver.model.redisdto.AlgorithmDataSynDto;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.RedisClient;
import com.pl.indexserver.untils.TagUtils;
import com.pl.mapper.ACommonCraftMapper;
import com.pl.mapper.QCommonCraftMapper;
import com.pl.mapper.TBusinessMapper;
import com.pl.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class QCommonCraftServiceImpl implements QCommonCraftService {

    private static final Logger logger = LoggerFactory.getLogger(QCommonCraftServiceImpl.class);
    @Autowired
    private QCommonCraftMapper qCommonCraftMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ACommonCraftMapper aCommonCraftMapper;
    @Autowired
    private TBusinessMapper tBusinessMapper;
    @Autowired
    private TBusinessConfigService tBusinessConfigService;
    @Autowired
    private SpeechcraftTagService speechcraftTagService;
    @Autowired
    private TBusinessFocusService tBusinessFocusService;
    @Autowired
    private TTSService ttsService;
    @Value("${redis.recordUpdateForSIP}")
    private String recordUpdateForSIP;
    @Value("${recordings.address}")
    private String ftpPath;
    @Value("${redis.algorithmDataSyn}")
    private String algorithmDataSyn;
    @Autowired
    private FileTransferService fileTransferService;//------------------- 后续调整 -----------------

    @Override
    public QCommonCraft selectByPrimaryKey(Long id) throws Exception {
        return qCommonCraftMapper.selectByPrimaryKey(id);
    }

    @Override
    public QCommonCraft selectQCommonCraftByCraftIdAndCompanyIdAndBusinessId(String craftId, Long companyId, Long businessId) throws Exception {
        return qCommonCraftMapper.selectByCraftIdAndCompanyIdAndBusinessId(craftId, companyId, businessId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Boolean deleteDetailByPrimaryKey(Long id) throws Exception {
        int i = qCommonCraftMapper.deleteDetailByPrimaryKey(id);
        return i > 0;
    }

    @Override
    public List<QCommonCraftDto> selectByCompanyIdAndBusinessId(Long companyId, Long businessId) throws Exception {
        if (businessId == null){
            businessId = 0L;
        }
        List<QCommonCraftDto> qCommonCraftDtos = qCommonCraftMapper.selectByCompanyIdAndBusinessId(companyId, businessId);
        if (!CollectionUtils.isEmpty(qCommonCraftDtos)) {
            for (QCommonCraftDto qCommonCraftModelDto:qCommonCraftDtos) {
                Date modifyDate = qCommonCraftModelDto.getModifyDate();
                if (StringUtils.isEmpty(modifyDate)) {
                    qCommonCraftModelDto.setModifyDate(qCommonCraftModelDto.getCreateDate());
                }
                Integer ruleType = qCommonCraftModelDto.getRuleType();
                if (ruleType == 1) {
                    qCommonCraftModelDto.setRuleTypeDetail("关键词触发");
                } else if (ruleType == 0) {
                    qCommonCraftModelDto.setRuleTypeDetail("系统触发");
                }
            }
        }
        return qCommonCraftDtos;
    }

    @Override
    public QCommonCraftDto selectDetailByPrimaryKey(Long id) throws Exception {
        QCommonCraftDto qCommonCraftDto = qCommonCraftMapper.selectDetailByPrimaryKey(id);
        Long companyId = qCommonCraftDto.getCompanyId();
        Long businessId = qCommonCraftDto.getBusinessId();
        // 获取当前公司所有话术标签
        List<SpeechcraftTagDto> speechcraftTagDtos = speechcraftTagService.getSpeechcraftTagDtoList(companyId, null);

        List<ACommonCraftDto> aCommonCraftDtos = qCommonCraftDto.getaCommonCraftDtos();
        if (aCommonCraftDtos.size() > 0) {
            for (ACommonCraftDto aCommonCraftDto:aCommonCraftDtos) {
                String content = aCommonCraftDto.getContent();
                String recordFile = aCommonCraftDto.getRecordFile();
                String recordDescribe = aCommonCraftDto.getRecordDescribe();

                String[] recordFiles = recordFile.split("&");
                String[] contents = content.split("&");
                String[] recordDescribes = recordDescribe.split("&");

                List<Map<String, String>> recordDetail = new ArrayList<>();
                for (int j = 0; j < contents.length; j++) {
                    Map<String, String> map = new HashMap<>();
                    String recordFileItem = recordFiles[j];
                    String recordName = null;
                    //处理录音文件
                    if (!TagUtils.isLabel(recordFileItem) && !"*".equals(recordFileItem)) {
                        recordName = ftpPath + "/" + companyId + "/BUSINESS-" + businessId + "/" + recordFileItem + "?" + new Date().getTime();
                    }
                    map.put("content", TagUtils.getLabel(contents[j], speechcraftTagDtos));
                    map.put("recordFile", recordName);
                    map.put("recordDescribe", recordDescribes[j]);
                    recordDetail.add(map);
                }
                aCommonCraftDto.setRecordDetail(recordDetail);
                aCommonCraftDto.setContent(TagUtils.disposeContent(content, speechcraftTagDtos));
            }
        }
        return qCommonCraftDto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Boolean insert(QCommonCraftDto qCommonCraftDto) throws Exception {

        // 处理通用话术问题相关数据
        String keyWord = qCommonCraftDto.getKeyWord();
        // 处理关键字数量
        if (StringUtils.isEmpty(keyWord)) {
            qCommonCraftDto.setKeyNum(0);
        } else {
            int keyNnum = keyWord.split("&").length;
            qCommonCraftDto.setKeyNum(keyNnum);
        }
        if(null==qCommonCraftDto.getType()){
            qCommonCraftDto.setType(CommonConstant.TYPE_1);
        }
        qCommonCraftDto.setStatus(CommonConstant.STATUS_0);
        qCommonCraftDto.setRuleType(CommonConstant.RULETYPE_1);
        qCommonCraftDto.setFlag(CommonConstant.FLAG_0);
        qCommonCraftDto.setCreateDate(new Date());
        int flag = qCommonCraftMapper.insertSelective(qCommonCraftDto);
        return flag > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Boolean updateByPrimaryKey(QCommonCraftDto qCommonCraftDto) throws Exception {
        String keyWord = qCommonCraftDto.getKeyWord();
        // 处理关键字数量
        if (StringUtils.isEmpty(keyWord)) {
            qCommonCraftDto.setKeyNum(0);
        } else {
            int keyNnum = keyWord.split("&").length;
            qCommonCraftDto.setKeyNum(keyNnum);
        }
        qCommonCraftDto.setModifyDate(new Date());
        qCommonCraftDto.setRuleType(null);
        
       // KnowledgeQuestionServiceImpl
        
    	logger.info(qCommonCraftDto.getId()+" 11 qCommonCraftDto.keyWord=" +qCommonCraftDto.getKeyWord() + "\n qCommonCraftDto.question=" + qCommonCraftDto.getQuestion()  );
        
    	    
       
        
        qCommonCraftDto.setKeyWord(KnowledgeQuestionServiceImpl.trimString(qCommonCraftDto.getKeyWord()));  
        
        qCommonCraftDto.setQuestion(KnowledgeQuestionServiceImpl.trimString(qCommonCraftDto.getQuestion()));  
        
        logger.info(qCommonCraftDto.getId()+" 22 qCommonCraftDto.keyWord=" +qCommonCraftDto.getKeyWord() + "\n qCommonCraftDto.question=" + qCommonCraftDto.getQuestion()  );
        
        
        
        int flag1 = qCommonCraftMapper.updateByPrimaryKeySelective(qCommonCraftDto);
        logger.info("=========flag1=" + flag1);
        if (qCommonCraftDto.getScore()!=null)
        {
	        ACommonCraftDto aCommonCraftDto = new ACommonCraftDto();
	        aCommonCraftDto.setScore(qCommonCraftDto.getScore());
	        aCommonCraftDto.setCraftId(qCommonCraftDto.getCraftId());
	        aCommonCraftDto.setBusinessId(qCommonCraftDto.getBusinessId());
	        aCommonCraftDto.setCompanyId(qCommonCraftDto.getCompanyId());	        
	        aCommonCraftMapper.updateByCraft(aCommonCraftDto);
        }
        
        return true;
    }

    @Override
    public Map<String, Object> selectFileDetailByPrimaryKey(Long id) throws Exception {
        Map<String, Object> map = new HashMap<>();
        QCommonCraftDto qCommonCraftDto = selectDetailByPrimaryKey(id);
        if (null == qCommonCraftDto) {
            return null;
        }
        // 拼凑文件路径
        String filePath = qCommonCraftDto.getCompanyId() + "/BUSINESS-" + qCommonCraftDto.getBusinessId();
        map.put("filePath", filePath);
        // 获取相关文件名
        List<ACommonCraftDto> aCommonCraftDtos = qCommonCraftDto.getaCommonCraftDtos();
        int size = aCommonCraftDtos.size();
        String[] fileNames = new String[size];
        for (int i = 0; i < size; i++) {
            String recordName = aCommonCraftDtos.get(i).getRecordName();
            if (null != recordName) {
                fileNames[i] = recordName;
            } else {
                fileNames[i] = "";
            }
        }
        map.put("fileNames", fileNames);
        return map;
    }

    @Override
    public CraftConfigDto selectCommonCraftConfig(Long companyId, Long businessId) {
        if (businessId == null) {
            businessId = 0L;
        }
        CraftConfigDto configInfo = qCommonCraftMapper.selectConfigByCompanyIdAndBusinessId(companyId, businessId);
        configInfo.setBusinessId(businessId);
        configInfo.setCompanyId(companyId);
        configInfo.setCommonCraftConfigList(qCommonCraftMapper.selectCommonConfigByCompanyIdAndBusinessId(companyId, businessId));
        if (configInfo.getBusinessId() > 0) {
            TBusiness tBusiness = tBusinessMapper.selectByPrimaryKey(businessId);
            if (tBusiness != null){
                configInfo.setName(tBusiness.getName());
                configInfo.setRemark(tBusiness.getRemark());
                configInfo.setTemplateType(tBusiness.getTemplateType());
            }
            CraftConfigDto businessConfig = tBusinessConfigService.getBusinessConfig(companyId, businessId);
            configInfo.setFocusConfig(businessConfig.getFocusConfig());
            configInfo.setScoreConfig(businessConfig.getScoreConfig());
            configInfo.setIntentConfig(businessConfig.getIntentConfig());
            configInfo.setPushConfig(businessConfig.getPushConfig());
        }
        configInfo.setSysBusinessTemplates(tBusinessConfigService.getSysBusinessTemplates());

        return configInfo;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public Boolean updateCommonCraftConfig(TmUser user, CraftConfigDto craftConfig) {
        if (craftConfig.getBusinessId() == null) {
            craftConfig.setBusinessId(0L);
        }
        CraftConfigDto oldConfig = this.selectCommonCraftConfig(user.getCompanyId(), craftConfig.getBusinessId());
        switch (craftConfig.getUnidentifiedStatus()) {
            case (0):
                // 设置停用
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 1, null, 1);
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 2, null, 1);
                break;
            case (1):
                // 设置不记次未识别
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 1, null, 1);
                qCommonCraftMapper.insertCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 2, 1, user.getUserid(), new Date(), null);
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 2, null, 0);
                break;
            case (2):
                // 设置不记次未识别
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 2, null, 1);
                qCommonCraftMapper.insertCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 1, 1, user.getUserid(), new Date(), null);
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 1, null, 0);
                break;
            default:
                break;
        }
        switch (craftConfig.getUnresponsiveStatus()) {
            case (0):
                // 设置停用
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 3, null, 1);
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 4, null, 1);
                break;
            case (1):
                // 设置不记次无应答
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 3, null, 1);
                qCommonCraftMapper.insertCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 4, 1, user.getUserid(), new Date(), null);
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 4, null, 0);
                break;
            case (2):
                // 设置分次无应答
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 4, null, 1);
                qCommonCraftMapper.insertCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 3, 1, user.getUserid(), new Date(), null);
                qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 3, null, 0);
                break;
            default:
                break;
        }

        // 设置通用话术
        if (!CollectionUtils.isEmpty(craftConfig.getCommonCraftConfigList())) {
            for (CommonCraftConfigDto commonCraftConfig : craftConfig.getCommonCraftConfigList()) {
                for (CommonCraftConfigDto oldCommonCraftConfig : oldConfig.getCommonCraftConfigList()) {
                    if (null != commonCraftConfig.getEnabledStatus() && commonCraftConfig.getId().equals(oldCommonCraftConfig.getId())) {
                        if (1 == commonCraftConfig.getEnabledStatus() && 0 == oldCommonCraftConfig.getEnabledStatus()) {
                            qCommonCraftMapper.insertCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 5, 1000, user.getUserid(), new Date(), commonCraftConfig.getId());
                            qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 5, commonCraftConfig.getId().toString(), 0);
                        } else if (0 == commonCraftConfig.getEnabledStatus()) {
                            qCommonCraftMapper.updateCommonConfig(user.getCompanyId(), craftConfig.getBusinessId(), 5, commonCraftConfig.getId().toString(), 1);
                        }
                        break;
                    }
                }
            }
        }
        if (craftConfig.getBusinessId() > 0) {
            tBusinessConfigService.saveBusinessConfig(user, craftConfig.getBusinessId(), TBusinessConfig.ConfigType.FOCUS.getCode(), craftConfig.getFocusConfig());
            tBusinessConfigService.saveBusinessConfig(user, craftConfig.getBusinessId(), TBusinessConfig.ConfigType.SCORE.getCode(), craftConfig.getScoreConfig());
            tBusinessConfigService.saveBusinessConfig(user, craftConfig.getBusinessId(), TBusinessConfig.ConfigType.INTENT.getCode(), craftConfig.getIntentConfig());
            tBusinessConfigService.saveBusinessConfig(user, craftConfig.getBusinessId(), TBusinessConfig.ConfigType.PUSH.getCode(), craftConfig.getPushConfig());
        }
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public void clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId, Map<Long, Long> flowIdMap) throws Exception {
        List<QCommonCraft> qCommonCraftDtos = qCommonCraftMapper.selectByParams(sourceCompanyId, sourceBusinessId);
        List<ACommonCraft> aCommonCrafts = aCommonCraftMapper.selectByCompanyIdAndBusinessId(sourceCompanyId, sourceBusinessId);
        logger.info("QCommonCraft size =={};ACommonCraft == {}",qCommonCraftDtos.size(),aCommonCrafts.size());
        Date date = new Date();
        for (QCommonCraft qCommonCraft:qCommonCraftDtos) {
            qCommonCraft.setId(null);
            qCommonCraft.setMsgtemplId(null);
            qCommonCraft.setCompanyId(targetCompanyId);
            qCommonCraft.setBusinessId(targetBusinessId);
            qCommonCraft.setCreateDate(date);
            qCommonCraft.setModifyDate(date);
            qCommonCraft.setUid(user.getUserid());
            if (!StringUtils.isEmpty(qCommonCraft.getFoucs())) {
                String focusNames = tBusinessFocusService.convertFocusIdsToFocusNames(sourceCompanyId, sourceBusinessId, qCommonCraft.getFoucs());
                String foucsIds = tBusinessFocusService.convertFocusNamesToFocusIds(user, targetBusinessId, focusNames);
                qCommonCraft.setFoucs(foucsIds);
            }
            if(0!=qCommonCraft.getJump()){
                qCommonCraft.setJump(flowIdMap.get(qCommonCraft.getJump()));
            }
            qCommonCraftMapper.insertSelective(qCommonCraft);
            logger.info("qCommonCraft id is {}",qCommonCraft.getId());
        }
        for (ACommonCraft aCommonCraft:aCommonCrafts) {
            aCommonCraft.setId(null);
            aCommonCraft.setCompanyId(targetCompanyId);
            aCommonCraft.setBusinessId(targetBusinessId);
            aCommonCraft.setCreateDate(date);
            aCommonCraft.setModifyDate(date);
            aCommonCraft.setUid(user.getUserid());
            aCommonCraftMapper.insertSelective(aCommonCraft);
            logger.info("aCommonCraft id is {}",aCommonCraft.getId());
        }
    }

    @Override
    public void pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum operation, Long id, Long companyId, Long businessId) {
        try {
            AlgorithmDataSynDto synDto = new AlgorithmDataSynDto(AlgorithmDataSynDto.TypeEnum.Q_COMMON_CRAFT, operation, id, companyId, businessId);
            redisClient.lpush(algorithmDataSyn, JSONObject.toJSONString(synDto));
        } catch (Exception ex) {
            logger.error("syn {} q_common_craft redis push error, id:{},companyId:{},businessId:{}", operation, id, companyId, businessId, ex);
        }
    }
}
