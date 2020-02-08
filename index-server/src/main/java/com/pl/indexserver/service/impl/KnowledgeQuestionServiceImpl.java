package com.pl.indexserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.KnowledgeAnswerDto;
import com.pl.indexserver.model.KnowledgeQuestionDto;
import com.pl.indexserver.model.SpeechcraftTagDto;
import com.pl.indexserver.model.redisdto.AlgorithmDataSynDto;
import com.pl.indexserver.service.KnowledgeQuestionService;
import com.pl.indexserver.service.SpeechcraftTagService;
import com.pl.indexserver.service.TBusinessFocusService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.RedisClient;
import com.pl.indexserver.untils.TagUtils;
import com.pl.mapper.DialogWorkflowMapper;
import com.pl.mapper.KnowledgeAnswerMapper;
import com.pl.mapper.KnowledgeQuestionMapper;
import com.pl.mapper.TBusinessMapper;
import com.pl.model.DialogWorkflow;
import com.pl.model.KnowledgeAnswer;
import com.pl.model.KnowledgeQuestion;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
public class KnowledgeQuestionServiceImpl implements KnowledgeQuestionService {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeQuestionServiceImpl.class);

    @Autowired
    private KnowledgeQuestionMapper knowledgeQuestionMapper;
    @Autowired
    private KnowledgeAnswerMapper knowledgeAnswerMapper;
    @Autowired
    private SpeechcraftTagService speechcraftTagService;
    @Value("${redis.recordUpdateForSIP}")
    private String recordUpdateForSIP;
    @Value("${recordings.address}")
    private String ftpPath;
    @Value("${redis.algorithmDataSyn}")
    private String algorithmDataSyn;
    @Autowired
    private TBusinessMapper tBusinessMapper;
    @Autowired
    private TBusinessFocusService tBusinessFocusService;
    @Autowired
    private DialogWorkflowMapper dialogWorkflowMapper;
    @Autowired
    private RedisClient redisClient;


    @Override
    public KnowledgeQuestion selectByPrimaryKey(Long id) {
        return knowledgeQuestionMapper.selectByPrimaryKey(id);
    }

    @Override
    public KnowledgeQuestion selectQuestionByKnowledgeId(String knowledgeId) {
        return knowledgeQuestionMapper.selectQuestionByKnowledgeId(knowledgeId);
    }

    @Override
    public List<KnowledgeQuestionDto> selectByCompanyIdAndBusinessId(Long companyId, Long businessId, Long workflowId, String name) {
        if (!StringUtils.isEmpty(name)) {
            name = name.replace("\"", "");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("companyId", companyId);
        map.put("businessId", businessId);
        map.put("workflowId", workflowId);
        map.put("name", name);
        List<KnowledgeQuestionDto> knowledgeQuestionDtos = knowledgeQuestionMapper.selectByCompanyIdAndBusinessId(map);
        if (!CollectionUtils.isEmpty(knowledgeQuestionDtos)) {
            for (KnowledgeQuestionDto knowledgeQuestionDto : knowledgeQuestionDtos){
                Date modifyDate = knowledgeQuestionDto.getModifyDate();
                if (StringUtils.isEmpty(modifyDate)) {
                    knowledgeQuestionDto.setModifyDate(knowledgeQuestionDto.getCreateDate());
                }
            }
        }
        return knowledgeQuestionDtos;
    }

    @Override
    public KnowledgeQuestionDto selectDetailByPrimaryKey(Long id) throws Exception {
        KnowledgeQuestionDto knowledgeQuestionDto = knowledgeQuestionMapper.selectDetaikByPrimaryKey(id);
        Long companyId = knowledgeQuestionDto.getCompanyId();
        Long businessId = knowledgeQuestionDto.getBusinessId();
        // 获取当前公司所有话术标签
        List<SpeechcraftTagDto> speechcraftTagDtos = speechcraftTagService.getSpeechcraftTagDtoList(companyId, null);
        List<KnowledgeAnswerDto> answerList = knowledgeQuestionDto.getAnswerList();
        if (!CollectionUtils.isEmpty(answerList)) {
            for (KnowledgeAnswerDto knowledgeAnswerDto:answerList) {
                String answer = knowledgeAnswerDto.getAnswer();
                String recordFile = knowledgeAnswerDto.getRecordFile();
                String recordDescribe = knowledgeAnswerDto.getRecordDescribe();
                String[] recordFiles = recordFile.split("&");
                String[] contents = answer.split("&");
                String[] recordDescribes = recordDescribe.split("&");
                List<Map<String, String>> recordDetail = new ArrayList<>();
                for (int j = 0; j < contents.length; j++) {
                    Map<String, String> map = new HashMap<>();
                    String recordFileItem = recordFiles[j];
                    String recordName = null;
                    //处理录音文件
                    if (!TagUtils.isLabel(recordFileItem) && !"*".equals(recordFileItem)) {
                        recordName = String.format("%s/%s/BUSINESS-%s/%s?%s", ftpPath, companyId, businessId, recordFileItem, new Date().getTime());
                        //recordName = ftpPath + "/" + companyId + "/BUSINESS-" + businessId + "/" + recordFileItem + "?" + new Date().getTime();
                    }
                    map.put("content", TagUtils.getLabel(contents[j], speechcraftTagDtos));
                    map.put("recordFile", recordName);
                    map.put("recordDescribe", recordDescribes[j]);
                    recordDetail.add(map);
                }
                knowledgeAnswerDto.setRecordDetail(recordDetail);
                knowledgeAnswerDto.setAnswer(TagUtils.disposeContent(answer, speechcraftTagDtos));
            }
        }
        return knowledgeQuestionDto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean insert(KnowledgeQuestionDto knowledgeQuestionDto) throws Exception {
        int flag1;
        // 处理KnowledgeQuestion模型数据
        knowledgeQuestionDto.setStatus(CommonConstant.STATUS_0);
        String keyWord = knowledgeQuestionDto.getKeyWord();
        // 处理关键字数量
        if (StringUtils.isEmpty(keyWord)) {
            knowledgeQuestionDto.setKeyNum(0);
        } else {
            int keyNnum = keyWord.split("&").length;
            knowledgeQuestionDto.setKeyNum(keyNnum);
        }
        knowledgeQuestionDto.setCreateDate(new Date());
        // 判断问答是否属于主流程  主流程问答 更改workflow为0
        if (knowledgeQuestionDto.getWorkflowId() != null) {
            DialogWorkflow dialogWorkflow = dialogWorkflowMapper.getPrimaryDiaLogWorkFlow(knowledgeQuestionDto.getBusinessId());
            if (dialogWorkflow != null && dialogWorkflow.getId().equals(knowledgeQuestionDto.getWorkflowId())) {
                knowledgeQuestionDto.setWorkflowId(0L);
            }
        }
        flag1 = knowledgeQuestionMapper.insertSelective(knowledgeQuestionDto);
        tBusinessMapper.updateModifyDateById(knowledgeQuestionDto.getBusinessId());
        return flag1 > 0;
    }

    
    public static String trimString(String keyWord)
    {
    	if (keyWord==null || keyWord.trim().length()==0)
    	{
    		return null;
    	}
    	StringBuffer buf = new StringBuffer();
    	 if(keyWord.contains("&"))
         {
         	String [] keys = keyWord.split("&");
         	Set<String> set = new HashSet<String>();
         	for (String key : keys)
         	{
         		key = key.trim();
         		if (key.length() > 0 && !set.contains(key))
         		{
         			set.add(key);
         			buf.append(key+"&");
         		}
         	}
         	String ret = buf.toString();
	       	 if (ret.endsWith("&"))
	       	 {
	       		 ret = ret.substring(0, ret.length()-1);
	       	 }
	       	return ret;
         }
    	 else
    	 {
    		 return keyWord.trim();
    	 }
    	 
    	
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean update(KnowledgeQuestionDto knowledgeQuestionDto) throws Exception {
        int flag1;
        // 处理KnowledgeQuestion模型数据
        knowledgeQuestionDto.setModifyDate(new Date());
        
        logger.info(knowledgeQuestionDto.getId()+" 11 knowledgeQuestionDto.keyWord=" +knowledgeQuestionDto.getKeyWord()
        		+ "\n knowledgeQuestionDto.question=" + knowledgeQuestionDto.getQuestion()  );
        
        knowledgeQuestionDto.setKeyWord(trimString(knowledgeQuestionDto.getKeyWord()));
        knowledgeQuestionDto.setQuestion(trimString(knowledgeQuestionDto.getQuestion()));
        logger.info(knowledgeQuestionDto.getId()+"22 knowledgeQuestionDto.keyWord=" +knowledgeQuestionDto.getKeyWord()
        		+ "\n knowledgeQuestionDto.question=" + knowledgeQuestionDto.getQuestion()  );
        
        
        String keyWord = knowledgeQuestionDto.getKeyWord();     
        
        // 处理关键字数量
        if (StringUtils.isEmpty(keyWord)) {
            knowledgeQuestionDto.setKeyNum(0);
        } else {
            int keyNnum = keyWord.split("&").length;
            knowledgeQuestionDto.setKeyNum(keyNnum);
        }
        // 判断问答是否属于主流程  主流程问答 更改workflow为0
        if (knowledgeQuestionDto.getWorkflowId() != null) {
            DialogWorkflow dialogWorkflow = dialogWorkflowMapper.getPrimaryDiaLogWorkFlow(knowledgeQuestionDto.getBusinessId());
            if (dialogWorkflow != null && dialogWorkflow.getId().equals(knowledgeQuestionDto.getWorkflowId())) {
                knowledgeQuestionDto.setWorkflowId(0L);
            }
        }
        flag1 = knowledgeQuestionMapper.updateByPrimaryKeySelective(knowledgeQuestionDto);
        KnowledgeAnswerDto knowledgeAnswerDto = new KnowledgeAnswerDto();
        knowledgeAnswerDto.setKnowledgeId(knowledgeQuestionDto.getKnowledgeId());
        knowledgeAnswerDto.setScore(knowledgeQuestionDto.getScore());
        knowledgeAnswerDto.setModifyDate(new Date());
        knowledgeAnswerDto.setMsgtemplId(knowledgeQuestionDto.getMsgtemplId());
        knowledgeAnswerDto.setAction(knowledgeQuestionDto.getAction());
        knowledgeAnswerDto.setLabel1(knowledgeQuestionDto.getFoucs());
        knowledgeAnswerMapper.updateByKnowledgeIdSelective(knowledgeAnswerDto);
        tBusinessMapper.updateModifyDateById(knowledgeQuestionDto.getBusinessId());
        return flag1 > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public boolean deleteKnowledgeDetail(Long id) throws Exception {
        KnowledgeQuestion knowledgeQuestion = selectByPrimaryKey(id);
        int i = knowledgeQuestionMapper.deleteDetailByPrimaryKey(id);
        tBusinessMapper.updateModifyDateById(knowledgeQuestion.getBusinessId());
        return i > 0;
    }

    @Override
    public Map<String, Object> selectFileDetailByPrimaryKey(Long id) throws Exception {
        Map<String, Object> map = new HashMap<>();
        KnowledgeQuestionDto knowledgeQuestionDto = selectDetailByPrimaryKey(id);
        if (null == knowledgeQuestionDto) {
            return null;
        }
        // 拼凑文件路径
        String filePath = knowledgeQuestionDto.getCompanyId() + "/BUSINESS-" + knowledgeQuestionDto.getBusinessId();
        map.put("filePath", filePath);
        // 获取相关文件名
        List<KnowledgeAnswerDto> answerList = knowledgeQuestionDto.getAnswerList();
        int size = answerList.size();
        String[] fileNames = new String[size];
        for (int i = 0; i < size; i++) {
            String recordName = answerList.get(i).getRecordFile();
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
    public List<KnowledgeAnswerDto> selectByKnowledgeId(String knowledgeId) throws Exception {
        return knowledgeAnswerMapper.selectByKnowledgeId(knowledgeId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Boolean updateByPrimaryKeySelective(List<KnowledgeAnswerDto> knowledgeAnswerDtos) throws Exception {
        if (CollectionUtils.isEmpty(knowledgeAnswerDtos)) {
            throw new NullPointerException();
        }
        boolean flag = false;
        for (KnowledgeAnswerDto knowledgeAnswerDto:knowledgeAnswerDtos) {
            KnowledgeAnswer knowledgeAnswer = new KnowledgeAnswer();
            knowledgeAnswer.setId(knowledgeAnswerDto.getId());
            knowledgeAnswer.setRecordFile(knowledgeAnswerDto.getRecordFile());
            int i1 = knowledgeAnswerMapper.updateByPrimaryKeySelective(knowledgeAnswer);
            if (i1 > 0) {
                flag = true;
            }
        }
        if (!flag) {
            throw new Exception();
        }
        return flag;
    }

    @Override
    public long countFileSizeByCompanyId(Long companyId) throws Exception {
        Long size = knowledgeAnswerMapper.countFileSizeByCompanyId(companyId);
        return size == null ? 0 : size;
    }

    @Override
    public int clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId, Map<Long, Long> flowIdsMap) {
        List<KnowledgeQuestion> knowledgeQuestions = knowledgeQuestionMapper.selectKnowledgeQuestionByCompanyIdAndBusinessId(sourceCompanyId, sourceBusinessId);
        Date date = new Date();
        for (KnowledgeQuestion knowledgeQuestion : knowledgeQuestions) {
            KnowledgeQuestion targetQuestion = new KnowledgeQuestion();
            BeanUtils.copyProperties(knowledgeQuestion, targetQuestion, "id", "modifyDate");
            targetQuestion.setKnowledgeId(UUID.randomUUID().toString().replace("-", "").toLowerCase());
            if (!StringUtils.isEmpty(targetQuestion.getFoucs())) {
                String focusNames = tBusinessFocusService.convertFocusIdsToFocusNames(sourceCompanyId, sourceBusinessId, targetQuestion.getFoucs());
                String foucsIds = tBusinessFocusService.convertFocusNamesToFocusIds(user, targetBusinessId, focusNames);
                targetQuestion.setFoucs(foucsIds);
            }
            targetQuestion.setCompanyId(targetCompanyId);
            targetQuestion.setBusinessId(targetBusinessId);
            targetQuestion.setWorkflowId(flowIdsMap.get(targetQuestion.getWorkflowId()));
            targetQuestion.setType(1);
            targetQuestion.setJump(0L);
            targetQuestion.setCreateDate(date);
            targetQuestion.setModifyDate(date);
//			targetQuestion.setWorkflowId(flowIdsMap.get(knowledgeQuestion.getWorkflowId()));
            knowledgeQuestionMapper.insertSelective(targetQuestion);
            // 获取当前问题下所有答案
            List<KnowledgeAnswer> answers = knowledgeAnswerMapper.selectKnowledgeAnswerByKnowledgeId(knowledgeQuestion.getKnowledgeId());
            for (KnowledgeAnswer answer : answers) {
                KnowledgeAnswer targetAnwer = new KnowledgeAnswer();
                BeanUtils.copyProperties(answer, targetAnwer, "id", "modifyDate");
                targetAnwer.setKnowledgeId(targetQuestion.getKnowledgeId());
                targetAnwer.setCompanyId(targetCompanyId);
                targetAnwer.setBusinessId(targetBusinessId);
                targetAnwer.setCreateDate(date);
                targetAnwer.setModifyDate(date);
                knowledgeAnswerMapper.insertSelective(targetAnwer);
            }
        }
        return 1;
    }

    @Override
    public void pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum operation, Long id, Long companyId, Long businessId) {
        try {
            AlgorithmDataSynDto synDto = new AlgorithmDataSynDto(AlgorithmDataSynDto.TypeEnum.KNOWLEDGE_QUESTION, operation, id, companyId, businessId);
            redisClient.lpush(algorithmDataSyn, JSONObject.toJSONString(synDto));
        } catch (Exception ex) {
        	logger.error("syn {} knowledge_question redis push error, id:{},companyId:{},businessId:{}", operation, id, companyId, businessId, ex);
        }
    }

    @Override
    public List<KnowledgeQuestion> selectQuestionList() {
        return knowledgeQuestionMapper.selectQuestionList();
    }
}
