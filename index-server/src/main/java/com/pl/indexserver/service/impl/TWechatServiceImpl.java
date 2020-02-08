package com.pl.indexserver.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.service.TWechatService;
import com.pl.indexserver.untils.RestClientUtil;
import com.pl.mapper.*;
import com.pl.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/7/25
 */
@Service
public class TWechatServiceImpl implements TWechatService {

    private static final Logger logger = LoggerFactory.getLogger(TWechatServiceImpl.class);

    @Autowired
    private TWechatRecordMapper tWechatRecordMapper;
    @Autowired
    private WorkflowLinkMapper workflowLinkMapper;
    @Autowired
    private WorkflowNodeMapper workflowNodeMapper;
    @Autowired
    private SpeechcraftMapper speechcraftMapper;
    @Autowired
    private TBusinessMapper businessMapper;
    @Autowired
    private KnowledgeAnswerMapper answerMapper;
    @Autowired
    private ACommonCraftMapper aCommonCraftMapper;


    @Override
    public WechatRespon selectWechatRecordByWechatIds(TWechatRecord tWechatRecord) {
        String robotWechatId = tWechatRecord.getRobotWechatid();
        String userWechatId = tWechatRecord.getUserWechatid();
        Long businessId = tWechatRecord.getBusinessId();
        String content = tWechatRecord.getContent();
        String startSession = tWechatRecord.getStartSession();
        WechatRespon wechatRespon = new WechatRespon();

        logger.info("==========businessId={}", businessId);
        TBusiness business = businessMapper.getBusinessName(businessId);
        if (StringUtils.isEmpty(business)) {
            wechatRespon.setCode(2);
            wechatRespon.setContentResp("此智库不存在");
            return wechatRespon;
        }
        Long companyId = business.getCompanyId();
        //通过robotWechatId, userWechatId, businessId控制唯一一条记录
        TWechatRecord wechatRecord = tWechatRecordMapper.selectOneWorkNodeByWechatIds(robotWechatId, userWechatId, businessId);
        logger.info("%%%%%%%%%%%wechatRecord={}", wechatRecord);

        if (StringUtils.isEmpty(wechatRecord)) {
            //没有记录，新增一条
            int recordCount = tWechatRecordMapper.insertSelective(tWechatRecord);
            logger.info("==========recordCount={}", recordCount);
        }
        //返回开场白话术
        //startSession为true
        if ("true".equals(startSession)){
            List<WorkflowNode> workflowNodes = workflowNodeMapper.selectNameByBusinessId(businessId);
            for (WorkflowNode workflowNode : workflowNodes) {
                if ("开场白".equals(workflowNode.getName())){
                    Speechcraft speechcraft = speechcraftMapper.getSpeechcraftByCraftId(workflowNode.getCraftId());
                    wechatRespon.setCode(0);
                    wechatRespon.setContentResp(speechcraft.getContent());
                    tWechatRecordMapper.updateByRecord(wechatRecord.getId(),workflowNode.getParamter());
                    return wechatRespon;
                }
            }
        }

        String worknodeId = wechatRecord.getWorknodeId();
        if (StringUtils.isEmpty(worknodeId)) {
            wechatRespon.setCode(2);
            wechatRespon.setContentResp("此节点为空~~");
            return wechatRespon;
        }
        //计算下一个节点，判断节点类型flag=0,9,10或者12
        WorkflowNode workflowNode = workflowNodeMapper.selecFlowIdtByParamter(worknodeId);
        Long flowId = workflowNode.getWorkflowId();
        List<WorkflowLink> workflowLinks = workflowLinkMapper.getWorkFlowLinkByFromId(worknodeId, flowId);
        //当前节点下没有线,相当于上次聊天结束了
        if (CollectionUtils.isEmpty(workflowLinks)) {
            wechatRespon.setCode(1);
            wechatRespon.setContentResp("当前节点下没有子节点~~");
            return wechatRespon;
        }

        //无论任何应答
        for (WorkflowLink workflowLink : workflowLinks) {
            if (1111 == workflowLink.getRule_id()) {
                Long nextId = workflowLink.getNextId();
                WorkflowNode nextWorkNode = workflowNodeMapper.selectFlagByNextWorkNodeId(nextId);
                String craftId = nextWorkNode.getCraftId();
                return getWechatRespon(wechatRespon, wechatRecord, nextId, craftId);
            }
        }

        //存在线的情况
        //1,先找无论任何应答，跳到下一节点，更新节点id
        //2,关键字触发，调算法接口rc=0,1,2,3,4  1流程2问答3系统通用04未匹配
        //3,0和4还要判断未识别的线的情况，跳到下一节点，更新节点id
        String responseIds = "";
        for (WorkflowLink workflowLink : workflowLinks) {
            if (1000 == workflowLink.getRule_id()) {
                responseIds += workflowLink.getResponseId()+",";
            }
        }
        if (responseIds.endsWith(",")) {
            responseIds = responseIds.substring(0, responseIds.length() - 1);
            //类型 1000:关键字触发,1111:无论任何应答,2222:无应答,3333:未识别,4444:其它,5555:核对
            String serviceUrl = "http://192.168.1.43:8001/get_response_id";
            String jsonParameter = "{\n" +
                                    "\"workflow_id\": " + flowId + ",\n" +
                                    "\"company_id\": " + companyId + ",\n" +
                                    "\"resp_ids\": [\n" +
                                    "      " + responseIds + "\n" +
                                    "  ],\n" +
                                    "\"match_resp_ids\": [],\n" +
                                    "\"business_id\": " + businessId + ",\n" +
                                    "\"content\": [\n" +
                                    "      \""+content+"\"\n" +
                                    "  ]\n" +
                                    "}";
            try {
                logger.info("%%%%%%%%%%%%%jsonParameter={}", jsonParameter);
                String result = RestClientUtil.postData(serviceUrl, jsonParameter);
                logger.info("%%%%%%%%%%%%%调算法返回的结果={}", result);
                    if (!StringUtils.isEmpty(result)) {
                        JSONObject resultJson = JSON.parseObject(result);
                        int rc = resultJson.getIntValue("rc");
                        /**
                         {
                         "rc": 1,
                         "content": {
                         "craft_id": "",
                         "company_id": 2070,
                         "business_id": 134680142,
                         "name": "无意向(送礼送健康)",
                         "match_key_word": "不需要",
                         "match_question": "",
                         "resp_id": 1733,
                         "req_content": "他不需要多想。"
                         }
                         */
                        //流程
                        if (rc == 1) {
                            String contentStr = resultJson.getString("content");
                            JSONObject jsonObject = JSONObject.parseObject(contentStr);
                            String respId = jsonObject.getString("resp_id");
                            for (WorkflowLink workflowLink : workflowLinks) {
                                Long responseId = workflowLink.getResponseId();
                                String s = String.valueOf(responseId);
                                if (s.equals(respId)){
                                    Long nextId = workflowLink.getNextId();
                                    WorkflowNode nextWorkNode = workflowNodeMapper.selectFlagByNextWorkNodeId(nextId);
                                    String craftId = nextWorkNode.getCraftId();
                                    //有可能是跳转节点，没有话术craftId
                                    if (StringUtils.isEmpty(craftId)){
                                        Long jump = nextWorkNode.getJump();
                                        Long jump1 = 0L;
                                        int flag = 10;
                                        WorkflowNode workflowNode1 = workflowNodeMapper.selectJumpByParamter(jump, jump1,flag);
                                        String craftId1 = workflowNode1.getCraftId();
                                        return getWechatRespon(wechatRespon, wechatRecord, nextId, craftId1);
                                    }
                                    return getWechatRespon(wechatRespon, wechatRecord, nextId, craftId);
                                }
                            }
                        }
                        /**
                         * {
                         "rc": 2,
                         "content": {
                         "craft_id": "7c831075680649cfb963ce3d4d86294c",
                         "company_id": 2074,
                         "business_id": 134680260,
                         "name": "宽带自行恢复",
                         "match_key_word": "宽带.*(好了|恢复|修复|自行恢复|解决)",
                         "match_question": "",
                         "type": 1,
                         "jump": 0,
                         "action": 1,
                         "score": 0,
                         "focus": "",
                         "msgtempl_id": 0,
                         "workflow_id": 0,
                         "vad_eos": -1,
                         "req_content": "没有灵犀啊。 不过可能是可能是那个宽带有问题吧，药药好了。 "
                         }
                         }
                         */
                        // 问答
                        if (rc == 2) {
                            String contentStr = resultJson.getString("content");
                            JSONObject jsonObject = JSONObject.parseObject(contentStr);
                            String craftId = jsonObject.getString("craft_id");
                            KnowledgeAnswer answer = answerMapper.getAnswerByKnowledgeId(craftId);
                            wechatRespon.setCode(0);
                            wechatRespon.setContentResp(answer.getAnswer());
                            return wechatRespon;
                        }
                        // 通用
                        if (rc == 3) {
                            String contentStr = resultJson.getString("content");
                            JSONObject jsonObject = JSONObject.parseObject(contentStr);
                            String craftId = jsonObject.getString("craft_id");
                            ACommonCraft aCommonCraft = aCommonCraftMapper.
                                    selectContentByCraftIdAndCompanyIdAndBusinessId(craftId, workflowNode.getCompanyId(), businessId);
                            wechatRespon.setCode(0);
                            wechatRespon.setContentResp(aCommonCraft.getContent());
                            return wechatRespon;
                        }
                        //未识别，未匹配
                        for (WorkflowLink workflowLink : workflowLinks) {
                            if (3333 == workflowLink.getRule_id()) {
                                Long nextId = workflowLink.getNextId();
                                WorkflowNode nextWorkNode = workflowNodeMapper.selectFlagByNextWorkNodeId(nextId);
                                String craftId = nextWorkNode.getCraftId();
                                //有可能是跳转节点，没有话术id
                                if (StringUtils.isEmpty(craftId)){
                                    Long jump = nextWorkNode.getJump();
                                    Long jump1 = 0L;
                                    int flag = 10;
                                    WorkflowNode workflowNode1 = workflowNodeMapper.selectJumpByParamter(jump, jump1, flag);
                                    String craftId1 = workflowNode1.getCraftId();
                                    return getWechatRespon(wechatRespon, wechatRecord, nextId, craftId1);
                                }
                                return getWechatRespon(wechatRespon, wechatRecord, nextId, craftId);
                            }
                        }
                        String respContent = resultJson.getString("content");
                        wechatRespon.setContentResp(respContent);
                    }
                return wechatRespon;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        wechatRespon.setCode(1);
        wechatRespon.setContentResp("未知错误~~");
        return wechatRespon;
    }

    /**
     * hgl
     * 获取下一个节点的话术，并返回内容
     * @param wechatRespon
     * @param wechatRecord
     * @param nextId
     * @param craftId
     * @return
     */
    private WechatRespon getWechatRespon(WechatRespon wechatRespon, TWechatRecord wechatRecord, Long nextId, String craftId) {
        Speechcraft speechcraft = speechcraftMapper.getSpeechcraftByCraftId(craftId);
        String respContent = speechcraft.getContent();
        tWechatRecordMapper.updateWorkNodeIdByRecord(wechatRecord.getId(), nextId);
        wechatRespon.setCode(0);
        wechatRespon.setContentResp(respContent);
        return wechatRespon;
    }
}