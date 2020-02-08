package com.pl.indexserver.service.xbms.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TCustomerrespService;
import com.pl.indexserver.service.xbms.CustomerrespService;
import com.pl.mapper.KnowledgeAnswerMapper;
import com.pl.mapper.KnowledgeQuestionMapper;
import com.pl.mapper.TCustomrespMapper;
import com.pl.mapper.xbms.CustomrespMapper;
import com.pl.mapper.xbms.UserQaMapper;
import com.pl.model.KnowledgeAnswer;
import com.pl.model.KnowledgeQuestion;
import com.pl.model.wx.KnowledgeAnswerResp;
import com.pl.model.wx.QuestionDto;
import com.pl.model.wx.TCustomresp;
import com.pl.model.wx.TUserQa;
import com.pl.model.xbms.Customresp;
import com.pl.model.xbms.UserQa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import sun.management.counter.perf.PerfInstrumentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/28 15:34
 * @Description
 */
@Service
public class CustomerrespServiceImpl implements CustomerrespService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerrespServiceImpl.class);

    @Autowired
    private KnowledgeAnswerMapper knowledgeAnswerMapper;
    @Autowired
    private CustomrespMapper customrespMapper;
    @Autowired
    private KnowledgeQuestionMapper knowledgeQuestionMapper;
    @Autowired
    private UserQaMapper userQaMapper;


    @Override
    public List<KnowledgeAnswerResp> answerListQry(String knowledgeId, Integer userId) {
        List<KnowledgeAnswerResp> knowledgeAnswers = new ArrayList<>();
        List<KnowledgeAnswerResp> customers = new ArrayList<>();
        //系统的
        List<KnowledgeAnswer> answerList = knowledgeAnswerMapper.selectAnswerByKnowledgeId(knowledgeId);
        logger.info("========answerList={}=========", answerList.size());
        if (!CollectionUtils.isEmpty(answerList)) {
            for (KnowledgeAnswer answer : answerList) {
                logger.info("=======answer.getAnswer()={}=======", answer.getAnswer());
                KnowledgeAnswerResp resp = new KnowledgeAnswerResp();
                resp.setId(answer.getId());
                resp.setKnowledgeId(answer.getKnowledgeId());
                resp.setAnswer(answer.getAnswer());
                knowledgeAnswers.add(resp);
            }
        }

        //自定义的
        List<Customresp> tCustomresps = customrespMapper.selectByUseridAndKnowledgeId(userId, knowledgeId);
        if (!CollectionUtils.isEmpty(tCustomresps)) {
            for (Customresp tCustomresp : tCustomresps) {
                KnowledgeAnswerResp resp = new KnowledgeAnswerResp();
                resp.setId((long) tCustomresp.getId());
                resp.setKnowledgeId(tCustomresp.getKnowledgeId());
                resp.setAnswer(tCustomresp.getAnsContent());
                customers.add(resp);
            }
        }
        knowledgeAnswers.addAll(customers);
        return knowledgeAnswers;
    }

    @Override
    public List<QuestionDto> questionListQry(Integer userId) {
        List<QuestionDto> questionDtos = new ArrayList<>();
        List<KnowledgeQuestion> questionList = knowledgeQuestionMapper.selectQuestionList();
        logger.info("========questionList={}=========", questionList.size());

        if (!CollectionUtils.isEmpty(questionList)) {
            for (KnowledgeQuestion question : questionList) {
                Long answerId = null;
                String ansContent = "";
                UserQa tUserQa = userQaMapper.selectAnswerId(question.getKnowledgeId(), userId);
                if (!StringUtils.isEmpty(tUserQa)) {
                    logger.info("========tUserQa={}==========", tUserQa.toString());
                    answerId = tUserQa.getAnswerId();
                    try {
                        //系统定义的回答
                        KnowledgeAnswer answer = knowledgeAnswerMapper.selectByAnswerIdAndKnowledgeId(answerId, question.getKnowledgeId());
                        if (!StringUtils.isEmpty(answer)) {
                            ansContent = answer.getAnswer();
                        }
                        //系统回答没有
                        //用户自定义的回答
                        Customresp tCustomresp = customrespMapper.selectByUserIdAndKnowId(new Long(answerId).intValue(), userId, question.getKnowledgeId());
                        if (!StringUtils.isEmpty(tCustomresp)) {
                            ansContent = tCustomresp.getAnsContent();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                QuestionDto knowledgeQuestion = new QuestionDto();
                knowledgeQuestion.setId(question.getId());
                knowledgeQuestion.setKnowledgeId(question.getKnowledgeId());
                knowledgeQuestion.setName(question.getName());
                knowledgeQuestion.setAnswerId(answerId);
                knowledgeQuestion.setAnsContent(ansContent);
                questionDtos.add(knowledgeQuestion);
            }

        }
        return questionDtos;
    }

    @Override
    public CheckSmsCodeResp customerRespAdd(Customresp tCustomresp) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = customrespMapper.addCustomerresp(tCustomresp);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("新增自定义回复失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("新增自定义回复成功");
        return resp;
    }
}
