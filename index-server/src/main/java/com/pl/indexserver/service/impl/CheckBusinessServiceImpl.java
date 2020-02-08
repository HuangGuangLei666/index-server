package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.CheckBusinessService;
import com.pl.mapper.*;
import com.pl.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckBusinessServiceImpl implements CheckBusinessService {

    @Autowired
    private DialogWorkflowMapper dialogWorkflowMapper;

    @Autowired
    private WorkflowNodeMapper workflowNodeMapper;

    @Autowired
    private TBusinessMapper tBusinessMapper;

    @Autowired
    private WorkflowLinkMapper workflowLinkMapper;

    @Autowired
    private QCommonCraftMapper qCommonCraftMapper;

    @Autowired
    private ACommonCraftMapper aCommonCraftMapper;

    @Autowired
    private SpeechcraftMapper speechcraftMapper;

    @Autowired
    private KnowledgeAnswerMapper knowledgeAnswerMapper;

    @Autowired
    private KnowledgeQuestionMapper knowledgeQuestionMapper;

    @Override
    public TBusiness getCheckBusiness(Long businessId, Long companyId) {
        return tBusinessMapper.selectByPrimaryKey(businessId);
    }

    @Override
    public String workNodeCheck(TBusiness tBusiness) {
        StringBuilder restString = new StringBuilder("小兵智库检测系统").append("\r\n");
        restString.append("智库【").append(tBusiness.getName()).append("】").append("配置检查结果：\r\n");
        if (StringUtils.isEmpty(tBusiness)) {
            restString.append("没有找到要检查的智库").append("\r\n");
            return restString.toString();
        }
        restString.append("#####【智库流程节点检测】#####").append("\r\n");
        Map<String, Speechcraft> speechcraftMap = new HashMap<>();
        Map<String, WorkflowNode> workflowNodeMap = new HashMap<>();
        Long companyId = tBusiness.getCompanyId();
        Long businessId = tBusiness.getId();
        //准备验证数据(初步验证三大模块的完整性)
        //节点话术
        List<Speechcraft> speechcrafts = speechcraftMapper.selectByCompanyIdAndBusinessId(companyId, businessId);
        speechcrafts.forEach(sp -> {
            if (StringUtils.isEmpty(sp.getRecordFile())) {
                restString.append("【流程节点】节点话术没有录音，话术名称：").append(sp.getName()).append("\r\n");
            } else {
                speechcraftMap.put(sp.getCraftId(), sp);
            }
        });

        Map<String, Integer> dialogWorkflowMap = new HashMap<>();
        List<DialogWorkflow> dialogWorkflows = dialogWorkflowMapper.getDialogWorkFlowList(tBusiness.getCompanyId(), tBusiness.getId());
        dialogWorkflows.forEach(dwf->{
            dialogWorkflowMap.put(dwf.getId()+ ":" + dwf.getBusinessId(), 1);
        });
        //流程节点
        List<WorkflowNode> workflowNodes = workflowNodeMapper.selectByCompanyIdAndBusinessId(companyId, businessId);
        workflowNodes.forEach(wn -> {
            switch (wn.getFlag()){
                case 0: case 9: case 10: case 11:
                    if (wn.getJump() > 0L && wn.getFlag() == 11){
                        restString.append("【流程节点】节点设置跳转流程错误，节点名称：").append(wn.getName()).append("\r\n");
                    }
                    if (speechcraftMap.containsKey(wn.getCraftId())){
                        workflowNodeMap.put(wn.getParamter() + ":" + wn.getWorkflowId(), wn);
                    } else {
                        restString.append("【流程节点】节点没有配置话术，节点名称：").append(wn.getName()).append("\r\n");
                    }
                    break;
                case 12:
                    if (!dialogWorkflowMap.containsKey(wn.getJump()+":"+wn.getBusinessId())){
                        restString.append("【流程节点】节点跳转的子流程没有找到，节点名称：").append(wn.getName()).append("\r\n");
                    }
                    workflowNodeMap.put(wn.getParamter() + ":" + wn.getWorkflowId(), wn);
                    break;
                default:
                    if (speechcraftMap.containsKey(wn.getCraftId())){
                        workflowNodeMap.put(wn.getParamter() + ":" + wn.getWorkflowId(), wn);
                    } else {
                        restString.append("【流程节点】节点没有配置话术，节点名称：").append(wn.getName()).append("\r\n");
                    }
            }
        });
        //流程连接线
        List<WorkflowLink> workflowLinks = workflowLinkMapper.selectByCompanyIdAndBusinessId(companyId, businessId);
        workflowLinks.forEach(wl -> {
            if (workflowNodeMap.containsKey(wl.getFromId() + ":" + wl.getWorkflowId()) &&
                    workflowNodeMap.containsKey(wl.getNextId() + ":" + wl.getWorkflowId())) {
                if (StringUtils.isEmpty(wl.getResponseId())) {
                    WorkflowNode beginNode = workflowNodeMap.get(wl.getFromId() + ":" + wl.getWorkflowId());
                    WorkflowNode endNode = workflowNodeMap.get(wl.getNextId() + ":" + wl.getWorkflowId());
                    restString.append("【").append(beginNode.getName()).append("】").append("节点与");
                    restString.append("【").append(endNode.getName()).append("】").append("之间的连线，响应方式没有配置").append("\r\n");
                }
            } else {
                restString.append("【流程节点】流程节点连线的首端或末端节点不存在, 连线ID:").append(wl.getId()).append("\r\n");
                restString.append(" 前端节点ID:").append(wl.getFromId()).append("后端节点ID：").append(wl.getNextId()).append("\r\n");
            }
        });

        //开场白
        DialogWorkflow fistWorkFlow = dialogWorkflowMapper.getFirstWorkNode(tBusiness.getId());
        if (null == fistWorkFlow) {
            restString.append("【流程节点】流程开场白没有找到");
        } else {
            //开场白节点
            WorkflowNode workflowNode = workflowNodeMapper.getWorkFlowNodeByFlag(fistWorkFlow.getId(), 10);
            //检查开场白节点话术
            checkWorkFlowNode(restString, workflowNode, speechcraftMap);
            //检查开场白后的节点和话术
        }
        return restString.toString();
    }

    @Override
    public String workLinkCheck(TBusiness tBusiness) {
        return null;
    }

    @Override
    public String knowledgeQACheck(TBusiness tBusiness) {
        StringBuilder restString = new StringBuilder("#####【智库智能问答检测】#####").append("\r\n");
        Map<String, KnowledgeAnswer> knowledgeAnswerMap = new HashMap<>();
        List<KnowledgeQuestion> knowledgeQuestions = knowledgeQuestionMapper.selectKnowledgeQuestionByCompanyIdAndBusinessId(
                tBusiness.getCompanyId(), tBusiness.getId());
        if (CollectionUtils.isEmpty(knowledgeQuestions)){
            restString.append("【智能问答】智库没有配置问答知识库，智库名称:").append(tBusiness.getName()).append("\r\n");
        }else {
            List<KnowledgeAnswer> knowledgeAnswers = knowledgeAnswerMapper.selectByCompanyIdBusId(tBusiness.getCompanyId(), tBusiness.getId());
            if (CollectionUtils.isEmpty(knowledgeAnswers)){
                restString.append("【智能问答】智库配置的问答知识库缺少答案，智库名称：").append(tBusiness.getName()).append("\r\n");
            }else {
                knowledgeAnswers.forEach(kda->{
                    if (StringUtils.isEmpty(kda.getRecordFile())){
                        restString.append("【智能问答】问题答案没有录音，内容：").append(kda.getAnswer()).append("\r\n");
                    }
                    knowledgeAnswerMap.put(kda.getKnowledgeId(), kda);
                });
                for (KnowledgeQuestion question: knowledgeQuestions) {
                    if (!knowledgeAnswerMap.containsKey(question.getKnowledgeId())){
                        restString.append("【智能问答】问答知识库问题没有找到答案，问题名称：").append(question.getName()).append("\r\n");
                    }
                }
            }
        }
        return restString.toString();
    }

    @Override
    public String systemCommonCheck(TBusiness tBusiness) {
        StringBuilder restString = new StringBuilder("#####【系统通用话术检测】#####").append("\r\n");
        Map<String, ACommonCraft> aCommonCraftMap = new HashMap<>();
        /*Map<String, Integer> commonCtaft = new HashMap<>();
        commonCtaft.put("10",1);
        commonCtaft.put("11",1);
        commonCtaft.put("12",1);
        commonCtaft.put("13",1);
        commonCtaft.put("20",1);
        commonCtaft.put("21",1);
        commonCtaft.put("22",1);
        commonCtaft.put("23",1);*/
        List<QCommonCraft> qCommonCrafts = qCommonCraftMapper.selectByParams(tBusiness.getCompanyId(), tBusiness.getId());
        List<QCommonCraft> sysQCommonCrafts = qCommonCraftMapper.selectByParams(tBusiness.getCompanyId(), 0L);
        qCommonCrafts.addAll(sysQCommonCrafts);
        if (CollectionUtils.isEmpty(qCommonCrafts)){
            restString.append("【通用话术】没有配置，智库名称：").append(tBusiness.getName()).append("\r\n");
        }else {
            if (qCommonCrafts.size() < 6){
                restString.append("【通用话术】通用话术最少要有6条配置，否则智库运行出错").append("\r\n");
            }
            List<ACommonCraft> aCommonCrafts = aCommonCraftMapper.selectByCompanyIdAndBusinessId(tBusiness.getCompanyId(), tBusiness.getId());
            List<ACommonCraft> sysACommonCrafts = aCommonCraftMapper.selectByCompanyIdAndBusinessId(tBusiness.getCompanyId(), 0L);
            aCommonCrafts.addAll(sysACommonCrafts);
            if (CollectionUtils.isEmpty(aCommonCrafts)){
                restString.append("【通用话术】缺少回答内容，智库名称：").append(tBusiness.getName()).append("\r\n");
            }else {
                aCommonCrafts.forEach(acf->{
                    if (StringUtils.isEmpty(acf.getRecordFile())){
                        restString.append("【通用话术】答案没有录音，内容：").append(acf.getContent()).append("\r\n");
                    }
                    aCommonCraftMap.put(acf.getCraftId(), acf);
                });
                for (QCommonCraft qCommonCraft: qCommonCrafts) {
                    if (!aCommonCraftMap.containsKey(qCommonCraft.getCraftId())){
                        restString.append("【通用话术】话术内容没有配置，话术名称：").append(qCommonCraft.getName()).append("\r\n");
                    }
                }
            }
        }
        return restString.toString();
    }

    private void checkWorkFlowNode(StringBuilder restString, WorkflowNode workflowNode, Map<String, Speechcraft> speechcraftMap) {
        if (null == workflowNode) {
            restString.append("【流程节点】核查节点时，传入节点为空").append("\r\n");
        } else {
            List<Speechcraft> speechcraftList = speechcraftMapper.selectOfCheckBusiness(workflowNode.getCraftId(), workflowNode.getBusinessId());
            if (CollectionUtils.isEmpty(speechcraftList)) {
                restString.append("【流程节点】节点话术缺失，节点名称:").append(workflowNode.getName()).append("\r\n");
            } else {
                speechcraftList.forEach(sf-> {
                    if (!speechcraftMap.containsKey(sf.getCraftId())){
                        restString.append("【流程节点】节点话术不存在！话术名称：").append(sf.getName()).append("\r\n");
                    }
                });
            }
        }

    }

}
