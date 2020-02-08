package com.pl.indexserver.service;

import com.pl.indexserver.model.ReturnMsg;
import com.pl.model.TmUser;

import java.util.Map;

public interface WorkFlowService {

    /**
     * 创建workflow
     * @param user 用户信息
     * @param userId 用户id
     * @param flowId 流程图id
     * @param parentId 父流程id
     * @param paramter 界面参数
     * @param companyId 公司id
     * @param businessId 业务id
     * @param name 流程名称
     * @param triggerOrder 触发指令
     * @return
     */
    ReturnMsg workFlowCreate(TmUser user,String userId, Long flowId, Long parentId, String paramter, Long companyId, Long businessId, String name, String triggerOrder);

    /**
     * 删除workflow
     * @param flowId
     * @param businessId
     * @return
     */
    ReturnMsg deleteWorkFlow(TmUser user, Long flowId, Long businessId);

    /**
     * 创建workNode,不存在创建，存在更新
     * @param name 节点名称
     * @param flowId 关联workflow表的bussinessId
     * @param companyId 公司ID
     * @param paramter 界面参数
     * @param interupt 是否支持打断
     * @param flag 结点状态
     * @param jump 跳转的下一个节点
     * @param speechcraftId 专有话术标识
     * @param score 节点评分
     * @return
     */
    ReturnMsg createFlowNode(TmUser user, String uid, String name,Long flowId,Long companyId,Long businessId,String paramter,Integer interupt,Integer flag,Long jump,String speechcraftId, Integer score);

    /**
     * 删除workflowNode
     * @param paramter
     * @param flowId
     * @return
     */
    ReturnMsg deleteWorkFlowNode(TmUser user, String paramter, Long flowId);


    /**
     * 创建节点链接，不存在创建，存在更新
     * @param fromWorkNodeId 上一个节点id
     * @param nextWorkNodeId 下一个节点id
     * @param workFlowId 业务id
     * @param companyId 公司id
     * @param ruleId   规则id
     * @param priority  相同fromworkNode的worklink优先级 1，2，3。。由高到低
     * @param byAlgorithm 是否由算法决策，0否，1是
     * @param state    worklink决策状态，1：拒绝，2同意，3考虑
     * @param responseId 问答库ID查询回答的问题的答案。
     * @return
     */
    ReturnMsg createWorkFlowLink(TmUser user ,String uid, Long fromWorkNodeId,Long nextWorkNodeId,Long workFlowId,
                                 Long companyId,Long businessId,Long ruleId,Integer priority,Integer byAlgorithm,Integer state,Long responseId);

    /**
     * 删除 workFlowLink
     * @param flowId
     * @param flowLinkId
     * @return
     */
    ReturnMsg deleteWorkFlowLink(TmUser user, Long flowId, Long flowLinkId);

    /**
     * 创建响应方式
     * @param user 用户
     * @param companyId 公司id
     * @param flowId  业务标识
     * @param name 响应方式名称
     * @param craft 触发的话术
     * @param keyword 关键词 用&符号隔开
     * @return
     */
    ReturnMsg createSpecialResponseModel(TmUser user, Long companyId, Long flowId,String name,String craft,String uid,String keyword);

    /**
     * 保存流程图，存在更新，不存在创建
     * @param businessId
     * @param paramter
     * @return
     */
    ReturnMsg saveFlowUIParam(TmUser user,Long flowId, Long businessId,String paramter);

    /**
     * 查询workflowDetails list
     * @param user 当前登陆账号
     * @param bussinessId 当前登陆账号
     * @return
     */
    ReturnMsg getWorkFlowList(TmUser user, Long bussinessId);

    /**
     * 查询流程树workflow tree
     * @param user 当前登陆账号
     * @param bussinessId 当前登陆账号
     * @return
     */
    ReturnMsg getWorkFlowTree(TmUser user, Long bussinessId);

    /**
     * 查询workflowUIParam
     * @param user 公司Id
     * @param flowId 流程id
     * @return
     */
    ReturnMsg getWorkFlowUIParam(TmUser user,Long flowId);

    /**
     * 复制智库流程
     * @param sourceCompanyId
     * @param sourceBusinessId
     * @param targetCompanyId
     * @param targetBusinessId
     * @param speechcraftCraftId
     * @param responseModeId
     * @throws Exception
     */
    Map<Long, Long> clone(Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId,Map<String, String> speechcraftCraftId,Map<Long, Long> responseModeId) throws Exception;


}
