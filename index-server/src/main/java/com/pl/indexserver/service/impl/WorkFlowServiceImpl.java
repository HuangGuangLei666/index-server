package com.pl.indexserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.PageFlowModel;
import com.pl.indexserver.model.PageFlowNodeModel;
import com.pl.indexserver.model.PageFlowNodeModel;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.UserOperateRecordService;
import com.pl.indexserver.service.WorkFlowService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.DateUtils;
import com.pl.indexserver.untils.OperateType;
import com.pl.mapper.*;
import com.pl.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {

    private static final Logger logger = LoggerFactory.getLogger(WorkFlowServiceImpl.class);
    @Autowired
    private DialogWorkflowMapper dialogWorkflowMapper;
    @Autowired
    private WorkflowNodeMapper workflowNodeMapper;
    @Autowired
    private WorkflowLinkMapper workflowLinkMapper;
    @Autowired
    private ResponseModeMapper responseModeMapper;
    @Autowired
    private UserOperateRecordService userOperateRecordService;

    //创建 dialogWorkFlow
    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 36000,rollbackFor = Exception.class) //启用事务管理，失败自动回滚
    public ReturnMsg workFlowCreate(TmUser user, String uid, Long flowId, Long parentId, String paramter, Long companyId, Long businessId, String name, String triggerOrder) {
        DialogWorkflow dialogWorkflow = new DialogWorkflow(companyId, businessId, name, paramter);
        dialogWorkflow.setTriggerOrder(triggerOrder);
        dialogWorkflow.setParentId(parentId);
        dialogWorkflow.setLevel(this.getDialogWorkflowLevel(parentId) + 1);
        if (!StringUtils.isEmpty(dialogWorkflow.getTriggerOrder())) {
            dialogWorkflow.setTriggerMode(1);
        } else {
            dialogWorkflow.setTriggerMode(0);
        }
        dialogWorkflow.setModifyDate(new Date());
        DialogWorkflow oldDialogWorkflow = null;
        if (flowId != null) {
            oldDialogWorkflow = dialogWorkflowMapper.selectByPrimaryKey(flowId);
        }
        Long rest = flowId;
        if (oldDialogWorkflow != null) {
            dialogWorkflow.setId(flowId);
            dialogWorkflowMapper.updateByPrimaryKeySelective(dialogWorkflow);
            String opInfo = String.format("流程名称:%s", oldDialogWorkflow.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, oldDialogWorkflow.getBusinessId().toString(), "/flow/modify",
                    CommonConstant.MODIFY_WORKFLOW, JSON.toJSONString(dialogWorkflow), OperateType.MODIFY, opInfo,
                    oldDialogWorkflow, dialogWorkflow);
        } else {
            dialogWorkflow.setSort(this.getDialogWorkflowMaxSort(parentId) + 1);
            dialogWorkflowMapper.insertSelective(dialogWorkflow);
            rest = dialogWorkflow.getId(); //获取插入成功后的自增主键，（mybatis xml文件配置）
            String opInfo = String.format("流程名称:%s", dialogWorkflow.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "/flow/create",
                    CommonConstant.CREATE_WORKFLOW, JSON.toJSONString(dialogWorkflow), OperateType.CREATE, opInfo, null, null);
        }
        if (!StringUtils.isEmpty(rest)) {
            return new ReturnMsg(0, dialogWorkflowMapper.selectByPrimaryKey(rest), "", "");
        }
        return new ReturnMsg(-1, "", "", "插入失败");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)//启用事务管理，失败自动回滚
    public ReturnMsg deleteWorkFlow(TmUser user, Long flowId, Long businessId) {
        ReturnMsg returnMsg;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flowId", flowId);
        jsonObject.put("businessId", businessId);
        DialogWorkflow workflow = dialogWorkflowMapper.getPrimaryDiaLogWorkFlow(businessId);
        if (workflow != null && workflow.getId().equals(flowId)) {
            return new ReturnMsg(1, "", "", "主流程不允许删除！");
        }
        int chilrenCount = dialogWorkflowMapper.selectDialogWorkFlowCountByParentId(businessId, flowId);
        if (chilrenCount > 0) {
            return new ReturnMsg(1, "", "", "存在子流程不允许删除！");
        }
        DialogWorkflow oldObj = dialogWorkflowMapper.selectByPrimaryKey(flowId);
        if (null == oldObj){
            return new ReturnMsg(1,"","","要删除的子流程不存在！");
        }
        int resultInt = dialogWorkflowMapper.deleteByFlowIdAndBusinessId(flowId, businessId);
        if (resultInt > 0) {
            workflowLinkMapper.deleteByFlowId(flowId);
            workflowNodeMapper.deleteByFlowId(flowId);
            returnMsg = new ReturnMsg(0, resultInt, "", "");
            jsonObject.put("Message", "删除成功");
            String opInfo = String.format("流程名称:%s", oldObj.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "/flow/delete",
                    CommonConstant.DELETE_WORKFLOW, JSON.toJSONString(jsonObject), OperateType.DELETE, opInfo, null, null);
        } else {
            jsonObject.put("Message", "删除失败");
            returnMsg = new ReturnMsg(-1, "", "", "删除流程图失败");
        }
        return returnMsg;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 36000,rollbackFor = Exception.class) //启用事务管理，失败自动回滚
    public ReturnMsg createFlowNode(TmUser user, String uid, String name, Long flowId, Long companyId, Long businessId, String paramter, Integer interupt, Integer flag, Long jump, String speechcraftId, Integer score) {
        WorkflowNode workflowNode = new WorkflowNode(name, flowId, companyId, businessId, paramter, interupt, flag, jump, speechcraftId, score);

            WorkflowNode workflowNode1 = workflowNodeMapper.selectByParamter(flowId, paramter);
            int rest;
            if (!StringUtils.isEmpty(workflowNode1)) {
                rest = workflowNodeMapper.updateByPrimaryKeySelective(workflowNode);
                String opInfo = String.format("节点名称:%s", workflowNode1.getName());
                userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, workflowNode1.getBusinessId().toString(), "/node/modify",
                        CommonConstant.MODIFY_WORKFLOWNODE, JSON.toJSONString(workflowNode), OperateType.MODIFY, opInfo,
                        workflowNode1, workflowNode);
            } else {
                rest = workflowNodeMapper.insertSelective(workflowNode);
                String opInfo = String.format("节点名称:%s", workflowNode.getName());
                userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, workflowNode.getBusinessId().toString(), "/node/create",
                        CommonConstant.CREATE_WORKFLOWNODE, JSON.toJSONString(workflowNode), OperateType.CREATE, opInfo,null, null);
            }
            if (rest >0){
                return new ReturnMsg(0, rest, "", "");
            }
            return new ReturnMsg(-1, "", "", "插入失败");
    }

    //删除工作节点
    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 36000,rollbackFor = Exception.class) //启用事务管理，失败自动回滚
    public ReturnMsg deleteWorkFlowNode(TmUser user, String paramter, Long flowId) {
        int restInt;
        ReturnMsg returnMsg;
        restInt = workflowLinkMapper.getWorkFlowLinksByWorkNodeId(Long.valueOf(paramter), flowId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("paramter", paramter);
        jsonObject.put("flowId", flowId);
        if (restInt > 0) {
            jsonObject.put("ErrorMsg", "当前节点有流程关联不允许删除");
            return new ReturnMsg(1, "", "", "当前节点有流程关联不允许删除！");
        }
        WorkflowNode oldObj = workflowNodeMapper.selectByParamter(flowId, paramter);
        restInt = workflowNodeMapper.deleteByPrimaryKey(paramter,flowId);
        if (restInt>0){
            returnMsg = new ReturnMsg(0, restInt, "", "");
            String opInfo = String.format("节点名称:%s", oldObj.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, oldObj.getBusinessId().toString(), "/node/delete",
                    CommonConstant.DELETE_WORKFLOWNODE, JSON.toJSONString(jsonObject), OperateType.DELETE, opInfo,null, null);
            return returnMsg;
        }
        return new ReturnMsg(-1, "", "", "节点删除失败");
    }

    //创建workLink,不存在就创建，存在就修改。
    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 36000,rollbackFor = Exception.class)
    public ReturnMsg createWorkFlowLink(TmUser user,String uid, Long fromWorkNodeId, Long nextWorkNodeId, Long workFlowId, Long companyId, Long businessId, Long ruleId, Integer priority, Integer byAlgorithm, Integer state, Long responseId) {
        WorkflowLink workflowLink = new WorkflowLink(fromWorkNodeId, nextWorkNodeId, workFlowId, companyId, businessId, ruleId, priority, byAlgorithm, state, responseId);
        WorkflowLink workflowLink1 = workflowLinkMapper.selectByPrimaryKey(workFlowId, ruleId);
        JSONObject object = new JSONObject();
        object.put("workLinkId", ruleId);
        object.put("fromWorkNodeId", fromWorkNodeId);
        object.put("nextWorkNodeId", nextWorkNodeId);
        object.put("workFlowId", workFlowId);
        int restInt;
        if (!StringUtils.isEmpty(workflowLink1)) {
            if (workFlowId.equals(workflowLink1.getWorkflowId()) && fromWorkNodeId.equals(workflowLink1.getFromId()) && responseId.equals(workflowLink1.getResponseId())) {
                return new ReturnMsg(1, "", "", "同一个节点不能有相同的应答方式");
            }
            restInt = workflowLinkMapper.updateByPrimaryKeySelective(workflowLink);
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, "0", "/link/modify",
                    CommonConstant.MODIFY_WORKFLOWLINK, JSON.toJSONString(object), OperateType.MODIFY, null,
                    workflowLink1, workflowLink);
        } else {
            restInt = workflowLinkMapper.insertSelective(workflowLink);
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, "0", "/link/create",
                    CommonConstant.CREATE_WORKFLOWLINK, JSON.toJSONString(workflowLink), OperateType.CREATE, null,null, null);
        }
        if (restInt>0){
            return new ReturnMsg(0, restInt, "", "");
        }
        return new ReturnMsg(-1,"","","新增节点链接失败！");
    }

    //删除worklink
    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 36000,rollbackFor = Exception.class)
    public ReturnMsg deleteWorkFlowLink(TmUser user, Long flowId, Long ruleId) {
        ReturnMsg returnMsg;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flowId", flowId);
        jsonObject.put("flowLink", ruleId);
        WorkflowLink oldObj = workflowLinkMapper.selectByPrimaryKey(flowId, ruleId);
        int delInt = workflowLinkMapper.deleteWorkLinkByWorkFlowId(flowId, ruleId);
        if (delInt>0){
            returnMsg = new ReturnMsg(0, delInt, "", "");
            jsonObject.put("Message", "删除成功");
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, "0", "/link/delete",
                    CommonConstant.DELETE_WORKFLOWLINK, JSON.toJSONString(jsonObject), OperateType.DELETE, null,null, null);
        }else {
            jsonObject.put("Message", "删除失败");
            returnMsg = new ReturnMsg(-1, "", "", "删除链接失败");
        }
        return returnMsg;
    }

    //创建响应方式
    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 36000,rollbackFor = Exception.class)
    public ReturnMsg createSpecialResponseModel(TmUser user,Long companyId, Long flowId, String name, String craft, String uid, String keyword) {
        Date date = DateUtils.getCurrentDate();
        int keySize = (keyword.split("&")).length;
        ResponseMode responseMode = new ResponseMode(name, companyId, flowId, keyword, uid, date, date, craft);
        responseMode.setRuleType(1000);
        responseMode.setKeyNum(keySize);
        ReturnMsg returnMsg;
        responseModeMapper.insertSelective(responseMode);
        Long restInt = responseMode.getId();
        if (!StringUtils.isEmpty(restInt)){
            returnMsg = new ReturnMsg(0, restInt, "", "");
//            String opInfo = String.format("响应方式id:%s", responseMode.getId());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, "0", "/link/createSpecial",
                    CommonConstant.CREATE_RESPONSEMODE, JSON.toJSONString(responseMode), OperateType.CREATE, null,null, null);
        }else {
            logger.error("createSpecialFlowLink is Error:");
            returnMsg = new ReturnMsg(-1, "", "", "新增特殊应答方式失败。");
        }
        return returnMsg;
    }

    //保存图表UI参数。
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public ReturnMsg saveFlowUIParam(TmUser user, Long flowId, Long businessId, String paramter) {
        DialogWorkflow dialogWorkflow = new DialogWorkflow();
        dialogWorkflow.setBusinessId(businessId);
        dialogWorkflow.setCompanyId(user.getCompanyId());
        dialogWorkflow.setParamter(paramter);
        dialogWorkflow.setModifyDate(new Date());
        ReturnMsg returnMsg;
        int restInt;
        DialogWorkflow dialogWorkflow1 = dialogWorkflowMapper.getDialogWorkflowByFlowId(user.getCompanyId(), flowId);
        if (!StringUtils.isEmpty(dialogWorkflow1)) {
            dialogWorkflow.setId(dialogWorkflow1.getId());
            restInt = dialogWorkflowMapper.updateByPrimaryKeySelective(dialogWorkflow);
            returnMsg = new ReturnMsg(0, restInt, "", "");
            String opInfo = String.format("流程名称:%s", dialogWorkflow1.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, "0", "/flow/saveParam",
                    CommonConstant.MODIFY_WORKFLOWUI, JSON.toJSONString(dialogWorkflow), OperateType.MODIFY, opInfo,
                    dialogWorkflow1, dialogWorkflow);

        } else {
            restInt = dialogWorkflowMapper.insertSelective(dialogWorkflow);
            returnMsg = new ReturnMsg(0, restInt, "", "");
            String opInfo = String.format("流程名称:%s", dialogWorkflow.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, "0", "/flow/saveParam",
                    CommonConstant.CREATE_WORKFLOWUI, JSON.toJSONString(dialogWorkflow), OperateType.CREATE, opInfo,null, null);
        }
        if (restInt > 0) {
            return returnMsg;
        }
        return new ReturnMsg(-1, "", "", "流程图操作异常！");

    }

    //查询当前登陆用户的所有工作流程。
    @Override
    public ReturnMsg getWorkFlowList(TmUser user, Long businessId) {
        ReturnMsg returnMsg;
        try {
            List<DialogWorkflow> dialogWorkflow = dialogWorkflowMapper.getDialogWorkFlowList(user.getCompanyId(), businessId);
            if (StringUtils.isEmpty(dialogWorkflow)) {
                returnMsg = new ReturnMsg(0, new ArrayList<>(), "", "查无数据！");
            } else {
                returnMsg = new ReturnMsg(0, dialogWorkflow, "", "");
            }
        } catch (Exception e) {
            logger.error("getWorkFlowList is Error:" + e.getMessage());
            returnMsg = new ReturnMsg(-1, "", "", "查询数据失败");
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg getWorkFlowTree(TmUser user, Long bussinessId) {
        ReturnMsg returnMsg;
        try {
            List<DialogWorkflow> dialogWorkflows = dialogWorkflowMapper.getDialogWorkFlowList(user.getCompanyId(), bussinessId);
            if (StringUtils.isEmpty(dialogWorkflows)) {
                returnMsg = new ReturnMsg(0, new ArrayList<>(), "", "查无数据！");
            } else {
                // 构建流程树
                ArrayList<DialogWorkflow> roots = new ArrayList<>();
                ArrayList<DialogWorkflow> childrens = new ArrayList<>();
                for (DialogWorkflow workflow : dialogWorkflows) {
                    if (0 == workflow.getParentId()) {
                        roots.add(workflow);
                    } else {
                        childrens.add(workflow);
                    }
                }
                roots = buildWorkFlowTree(roots, childrens);
                returnMsg = new ReturnMsg(0, roots, "", "");
            }
        } catch (Exception e) {
            logger.error("getWorkFlowTree is Error:" + e.getMessage());
            returnMsg = new ReturnMsg(-1, "", "", "查询数据失败");
        }
        return returnMsg;
    }

    //获取上次保存的Ui界面图
    @Override
    public ReturnMsg getWorkFlowUIParam(TmUser user, Long flowId) {
        ReturnMsg returnMsg;
        try {
            DialogWorkflow dialogWorkflow = dialogWorkflowMapper.getDialogWorkflowByFlowId(user.getCompanyId(), flowId);
            if (StringUtils.isEmpty(dialogWorkflow)) {
                returnMsg = new ReturnMsg(0,null, "", "查无数据！");
            } else {
                returnMsg = new ReturnMsg(0, dialogWorkflow.getParamter(), "", "");
            }
        } catch (Exception e) {
            logger.error("getWorkFlowDetail is Error:" + e.getMessage());
            returnMsg = new ReturnMsg(-1, "", "", "查询数据失败");
        }
        return returnMsg;
    }

    /**
     * 构建流程树
     *
     * @param roots
     * @param childrens
     * @return
     */
    private ArrayList<DialogWorkflow> buildWorkFlowTree(ArrayList<DialogWorkflow> roots, ArrayList<DialogWorkflow> childrens) {
        for (DialogWorkflow root : roots) {
            ArrayList<DialogWorkflow> tempChildrens = new ArrayList<>();
            ArrayList<DialogWorkflow> otherChildrens = new ArrayList<>();
            for (DialogWorkflow children : childrens) {
                if (root.getId().equals(children.getParentId())) {
                    tempChildrens.add(children);
                } else {
                    otherChildrens.add(children);
                }
            }
            root.setChildren(buildWorkFlowTree(tempChildrens, otherChildrens));
        }
        return roots;
    }

    /**
     * 获取流程层级
     *
     * @param parentId
     * @return
     */
    private Integer getDialogWorkflowLevel(Long parentId) {
        if (parentId == null || 0 == parentId) {
            return 0;
        }
        DialogWorkflow workflow = dialogWorkflowMapper.selectByPrimaryKey(parentId);
        if (workflow != null) {
            return workflow.getLevel();
        } else {
            return 0;
        }
    }

    /**
     * 获取流程排序最大值
     *
     * @return
     */
    private Integer getDialogWorkflowMaxSort(Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        return dialogWorkflowMapper.selectDialogWorkflowMaxSort(parentId);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public Map<Long, Long> clone(Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId, Map<String, String> speechcraftCraftIdMap, Map<Long, Long> responseModeIdMap) throws Exception {
        Map<Long, Long> flowsIdMap = cloneDialogWorkflows(sourceCompanyId, sourceBusinessId, targetCompanyId, targetBusinessId);
        cloneWorkflowNodes(sourceCompanyId, sourceBusinessId, targetCompanyId, targetBusinessId, flowsIdMap, speechcraftCraftIdMap);
        cloneWorkflowLinks(sourceCompanyId, sourceBusinessId, targetCompanyId, targetBusinessId, flowsIdMap, responseModeIdMap);
        dialogWorkflowsCorrectionByClone(targetCompanyId, targetBusinessId, flowsIdMap, speechcraftCraftIdMap);
        return flowsIdMap;
    }

    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    protected Map<Long, Long> cloneDialogWorkflows(Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId) throws Exception {
        Map<Long, Long> flowsIdMap = new HashMap<>();
        Date date = new Date();
        List<DialogWorkflow> dialogWorkflows = dialogWorkflowMapper.getDialogWorkFlowDetilList(sourceCompanyId, sourceBusinessId);
        for (DialogWorkflow dialogWorkflow : dialogWorkflows) {
            Long sourceId = dialogWorkflow.getId();
            dialogWorkflow.setId(null);
            dialogWorkflow.setCompanyId(targetCompanyId);
            dialogWorkflow.setBusinessId(targetBusinessId);
            dialogWorkflow.setModifyDate(date);
            dialogWorkflowMapper.insertSelective(dialogWorkflow);
            flowsIdMap.put(sourceId, dialogWorkflow.getId());
        }
        return flowsIdMap;
    }

    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    protected void cloneWorkflowNodes(Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId, Map<Long, Long> flowsIdMap, Map<String, String> speechcraftCraftIdMap) throws Exception {
        List<WorkflowNode> workflowNodes = workflowNodeMapper.selectByCompanyIdAndBusinessId(sourceCompanyId, sourceBusinessId);
        for (WorkflowNode workflowNode : workflowNodes) {
            String craftId = workflowNode.getCraftId();
            workflowNode.setId(null);
            workflowNode.setCompanyId(targetCompanyId);
            workflowNode.setBusinessId(targetBusinessId);
            workflowNode.setWorkflowId(flowsIdMap.get(workflowNode.getWorkflowId()));
            if (!StringUtils.isEmpty(craftId)) {
                String newCraftId = speechcraftCraftIdMap.get(craftId);
                if (!StringUtils.isEmpty(newCraftId)) {
                    workflowNode.setCraftId(newCraftId);
                }
            }
            if (12 == workflowNode.getFlag()) {
                workflowNode.setJump(flowsIdMap.get(workflowNode.getJump()));
            }
            workflowNodeMapper.insertSelective(workflowNode);
        }
    }

    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    protected void cloneWorkflowLinks(Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId, Map<Long, Long> flowsIdMap, Map<Long, Long> responseModeIdMap) throws Exception {
        List<WorkflowLink> workflowLinks = workflowLinkMapper.selectByCompanyIdAndBusinessId(sourceCompanyId, sourceBusinessId);
        for (WorkflowLink workflowLink : workflowLinks) {
            workflowLink.setId(null);
            workflowLink.setCompanyId(targetCompanyId);
            workflowLink.setBusinessId(targetBusinessId);
            workflowLink.setWorkflowId(flowsIdMap.get(workflowLink.getWorkflowId()));
            Long responseId = workflowLink.getResponseId();
            if (null != responseId) {
                Long newResponseModeId = responseModeIdMap.get(responseId);
                workflowLink.setResponseId(null==newResponseModeId?responseId:newResponseModeId);
            }
            workflowLinkMapper.insertSelective(workflowLink);
        }
    }

    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    protected int dialogWorkflowsCorrectionByClone(Long targetCompanyId, Long targetBusinessId, Map<Long, Long> flowsIdMap, Map<String, String> speechcraftCraftIdMap) throws Exception {
        List<DialogWorkflow> newDialogWorkflows = dialogWorkflowMapper.getDialogWorkFlowDetilList(targetCompanyId, targetBusinessId);
        for (DialogWorkflow newDialogWorkflow : newDialogWorkflows) {
            String paramter = newDialogWorkflow.getParamter();
            PageFlowModel pageFlowModel = JSONObject.parseObject(paramter, PageFlowModel.class);
            if(null!=pageFlowModel.nodes){
                for (Map.Entry<String, PageFlowNodeModel> pageFlowNode : pageFlowModel.nodes.entrySet()) {
                    PageFlowNodeModel pageFlowNodeModel = pageFlowNode.getValue();
                    if(null!=pageFlowNodeModel.change_data){
                        pageFlowNodeModel.change_data.flowId = flowsIdMap.get(pageFlowNodeModel.change_data.flowId);
                        pageFlowNodeModel.change_data.businessId = targetBusinessId;
                        pageFlowNodeModel.change_data.speechcraftId = speechcraftCraftIdMap.get(pageFlowNodeModel.change_data.speechcraftId);
                    }

                }
            }
            newDialogWorkflow.setParamter(JSONObject.toJSONString(pageFlowModel));
            newDialogWorkflow.setParentId(flowsIdMap.get(newDialogWorkflow.getParentId()));
            dialogWorkflowMapper.updateByPrimaryKeySelective(newDialogWorkflow);
        }
        return newDialogWorkflows.size();
    }

}
