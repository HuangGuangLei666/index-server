package com.pl.indexserver.web;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.algorithm.AlgorithmSecretDto;
import com.pl.indexserver.service.AlgorithmDataSynService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.RedisClient;
import com.pl.indexserver.untils.XXTEAUtil;
import com.pl.model.AlgorithmKnowledgeQuestion;
import com.pl.model.AlgorithmQCommonCraft;
import com.pl.model.AlgorithmResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 算法数据同步
 *
 * @Author bei.zhang
 * @Date 2018/11/20 15:03
 */
@RestController
@RequestMapping("/busiManagement/algorithm")
public class AlgorithmDataSynController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessContorller.class);

    // 加密数据生效时间
    private static final Long EFFECTIVE_DURATION = 10L;

    @Autowired
    private AlgorithmDataSynService algorithmDataSynService;

    @Autowired
    private RedisClient redisClient;

    /**
     * 同步智库问题
     *
     * @param secretDto
     * @return
     */
    @RequestMapping("/syn/synKnowledgeQuestion")
    public ReturnMsg synKnowledgeQuestion(AlgorithmSecretDto secretDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            Long interval = (System.currentTimeMillis() - secretDto.getTimestamp()) / (1000 * 60);
            if (interval > EFFECTIVE_DURATION) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (StringUtils.isEmpty(secretDto.getSecret())) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is null!");
                return returnMsg;
            }
            String decryptData = XXTEAUtil.decryptData(secretDto.getSecret());
            if (StringUtils.isEmpty(decryptData)) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret decrypt is error!");
                return returnMsg;
            }
            AlgorithmKnowledgeQuestion knowledgeQuestion = JSONObject.parseObject(decryptData, AlgorithmKnowledgeQuestion.class);
            if (knowledgeQuestion == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (knowledgeQuestion.getCompanyId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("companyId is null!");
                return returnMsg;
            }
            if (knowledgeQuestion.getBusinessId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("businessId is null!");
                return returnMsg;
            }
            if (knowledgeQuestion.getOriginalId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("originalId is null!");
                return returnMsg;
            }
            int count = algorithmDataSynService.synKnowledgeQuestion(knowledgeQuestion);
            if (count > 0) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(CommonConstant.OPERATION_SUCCEED);
            } else {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setContent("synKnowledgeQuestion is error");
            }
        } catch (Exception ex) {
            logger.error("synKnowledgeQuestion error, request info:{}", secretDto, ex);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("同步智库问题异常");
        }
        return returnMsg;
    }

    /**
     * 同步删除智库问题
     *
     * @param secretDto
     * @return
     */
    @RequestMapping("/syn/synDeleteKnowledgeQuestion")
    public ReturnMsg synDeleteKnowledgeQuestion(AlgorithmSecretDto secretDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            Long interval = (System.currentTimeMillis() - secretDto.getTimestamp()) / (1000 * 60);
            if (interval > EFFECTIVE_DURATION) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (StringUtils.isEmpty(secretDto.getSecret())) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is null!");
                return returnMsg;
            }
            String decryptData = XXTEAUtil.decryptData(secretDto.getSecret());
            if (StringUtils.isEmpty(decryptData)) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret decrypt is error!");
                return returnMsg;
            }
            AlgorithmKnowledgeQuestion knowledgeQuestion = JSONObject.parseObject(decryptData, AlgorithmKnowledgeQuestion.class);
            if (knowledgeQuestion == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (knowledgeQuestion.getCompanyId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("companyId is null!");
                return returnMsg;
            }
            if (knowledgeQuestion.getBusinessId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("businessId is null!");
                return returnMsg;
            }
            if (knowledgeQuestion.getOriginalId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("originalId is null!");
                return returnMsg;
            }
            int count = algorithmDataSynService.synDeleteKnowledgeQuestion(knowledgeQuestion.getCompanyId(), knowledgeQuestion.getBusinessId(), knowledgeQuestion.getOriginalId());
            if (count > 0) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(CommonConstant.OPERATION_SUCCEED);
            } else {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setContent("synDeleteKnowledgeQuestion is error");
            }
        } catch (Exception ex) {
            logger.error("synDeleteKnowledgeQuestion error, request info:{}", secretDto, ex);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("同步删除智库问题异常");
        }
        return returnMsg;
    }

    /**
     * 同步通用话术
     *
     * @param secretDto
     * @return
     */
    @RequestMapping("/syn/synQCommonCraft")
    public ReturnMsg synQCommonCraft(AlgorithmSecretDto secretDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            Long interval = (System.currentTimeMillis() - secretDto.getTimestamp()) / (1000 * 60);
            if (interval > EFFECTIVE_DURATION) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (StringUtils.isEmpty(secretDto.getSecret())) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is null!");
                return returnMsg;
            }
            String decryptData = XXTEAUtil.decryptData(secretDto.getSecret());
            if (StringUtils.isEmpty(decryptData)) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret decrypt is error!");
                return returnMsg;
            }
            AlgorithmQCommonCraft qCommonCraft = JSONObject.parseObject(decryptData, AlgorithmQCommonCraft.class);
            if (qCommonCraft == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (qCommonCraft.getCompanyId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("companyId is null!");
                return returnMsg;
            }
            if (qCommonCraft.getBusinessId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("businessId is null!");
                return returnMsg;
            }
            if (qCommonCraft.getOriginalId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("originalId is null!");
                return returnMsg;
            }
            int count = algorithmDataSynService.synQCommonCraft(qCommonCraft);
            if (count > 0) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(CommonConstant.OPERATION_SUCCEED);
            } else {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setContent("synQCommonCraft is error");
            }
        } catch (Exception ex) {
            logger.error("synQCommonCraft error, request info:{}", secretDto, ex);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("同步通用话术异常");
        }
        return returnMsg;
    }

    /**
     * 同步删除通用话术
     *
     * @param secretDto
     * @return
     */
    @RequestMapping("/syn/synDeleteQCommonCraft")
    public ReturnMsg synDeleteQCommonCraft(AlgorithmSecretDto secretDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            Long interval = (System.currentTimeMillis() - secretDto.getTimestamp()) / (1000 * 60);
            if (interval > EFFECTIVE_DURATION) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (StringUtils.isEmpty(secretDto.getSecret())) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is null!");
                return returnMsg;
            }
            String decryptData = XXTEAUtil.decryptData(secretDto.getSecret());
            if (StringUtils.isEmpty(decryptData)) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret decrypt is error!");
                return returnMsg;
            }
            AlgorithmQCommonCraft qCommonCraft = JSONObject.parseObject(decryptData, AlgorithmQCommonCraft.class);
            if (qCommonCraft == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (qCommonCraft.getCompanyId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("companyId is null!");
                return returnMsg;
            }
            if (qCommonCraft.getBusinessId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("businessId is null!");
                return returnMsg;
            }
            if (qCommonCraft.getOriginalId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("originalId is null!");
                return returnMsg;
            }
            int count = algorithmDataSynService.synDeleteQCommonCraft(qCommonCraft.getCompanyId(), qCommonCraft.getBusinessId(), qCommonCraft.getOriginalId());
            if (count > 0) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(CommonConstant.OPERATION_SUCCEED);
            } else {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setContent("synDeleteQCommonCraft is error");
            }
        } catch (Exception ex) {
            logger.error("synDeleteQCommonCraft error, request info:{}", secretDto, ex);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("同步删除通用话术异常");
        }
        return returnMsg;
    }

    /**
     * 同步响应方式异常
     *
     * @param secretDto
     * @return
     */
    @RequestMapping("/syn/synResponseMode")
    public ReturnMsg synResponseMode(AlgorithmSecretDto secretDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            Long interval = (System.currentTimeMillis() - secretDto.getTimestamp()) / (1000 * 60);
            if (interval > EFFECTIVE_DURATION) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (StringUtils.isEmpty(secretDto.getSecret())) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is null!");
                return returnMsg;
            }
            String decryptData = XXTEAUtil.decryptData(secretDto.getSecret());
            if (StringUtils.isEmpty(decryptData)) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret decrypt is error!");
                return returnMsg;
            }
            AlgorithmResponseMode responseMode = JSONObject.parseObject(decryptData, AlgorithmResponseMode.class);
            if (responseMode == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (responseMode.getCompanyId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("companyId is null!");
                return returnMsg;
            }
            if (responseMode.getBusinessId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("businessId is null!");
                return returnMsg;
            }
            if (responseMode.getOriginalId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("originalId is null!");
                return returnMsg;
            }
            int count = algorithmDataSynService.synResponseMode(responseMode);
            if (count > 0) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(CommonConstant.OPERATION_SUCCEED);
            } else {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setContent("synResponseMode is error");
            }
        } catch (Exception ex) {
            logger.error("synResponseMode error, request info:{}", secretDto, ex);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("同步响应方式异常");
        }
        return returnMsg;
    }


    /**
     * 同步删除响应方式异常
     *
     * @param secretDto
     * @return
     */
    @RequestMapping("/syn/synDeleteResponseMode")
    public ReturnMsg synDeleteResponseMode(AlgorithmSecretDto secretDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            Long interval = (System.currentTimeMillis() - secretDto.getTimestamp()) / (1000 * 60);
            if (interval > EFFECTIVE_DURATION) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (StringUtils.isEmpty(secretDto.getSecret())) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is null!");
                return returnMsg;
            }
            String decryptData = XXTEAUtil.decryptData(secretDto.getSecret());
            if (StringUtils.isEmpty(decryptData)) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret decrypt is error!");
                return returnMsg;
            }
            AlgorithmResponseMode responseMode = JSONObject.parseObject(decryptData, AlgorithmResponseMode.class);
            if (responseMode == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("secret is invalid!");
                return returnMsg;
            }
            if (responseMode.getCompanyId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("companyId is null!");
                return returnMsg;
            }
            if (responseMode.getBusinessId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("businessId is null!");
                return returnMsg;
            }
            if (responseMode.getOriginalId() == null) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("originalId is null!");
                return returnMsg;
            }
            int count = algorithmDataSynService.synDeleteResponseMode(responseMode.getCompanyId(), responseMode.getBusinessId(), responseMode.getOriginalId());
            if (count > 0) {
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(CommonConstant.OPERATION_SUCCEED);
            } else {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setContent("synDeleteResponseMode is error");
            }
        } catch (Exception ex) {
            logger.error("synDeleteResponseMode error, request info:{}", secretDto, ex);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("同步删除响应方式异常");
        }
        return returnMsg;
    }
}
