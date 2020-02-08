package com.pl.mapper;

import com.pl.model.WorkflowNode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

import java.util.List;

public interface WorkflowNodeMapper {

    int deleteByPrimaryKey(@Param("paramter")String paramter,@Param("workflowId")Long workflowId);

    int insert(WorkflowNode record);

    int insertSelective(WorkflowNode record);

    WorkflowNode selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WorkflowNode record);

    int updateByPrimaryKey(WorkflowNode record);

    WorkflowNode selectByParamter(@Param("flowId")Long flowId, @Param("paramter") String param);

    int deleteByFlowId(@Param("flowId") Long flowId);

    /**
     * 根据公司标识和智库标识查询相应流程节点数据
     * @param companyId 公司标识
     * @param businessId    智库标识
     * @return
     */
    List<WorkflowNode> selectByCompanyIdAndBusinessId(@Param("companyId")Long companyId, @Param("businessId") Long businessId);

    List<WorkflowNode> selectWorkNodeByFlowId(@Param("businessId") Long businessId, @Param("flowId") Long flowId);

    WorkflowNode getWorkFlowNodeByFlag(@Param("workflowId") Long flowId, @Param("flag")Integer flag);

    WorkflowNode selectFlagByWorkNodeId(String worknodeId);

    /**
     * hgl
     * 当前节点的下一个节点
     * @param nextId
     * @return
     */
    WorkflowNode selectFlagByNextWorkNodeId(Long nextId);

    /**
     * hgl
     * 获取当前节点
     * @param worknodeId
     * @return
     */
    WorkflowNode selecFlowIdtByParamter(String worknodeId);

    /**
     * hgl
     * 下一个节点类型为跳转节点flag=12，获取下一节点的开始节点flag=10
     * @param jump
     * @param jump1
     * @param flag
     * @return
     */
    WorkflowNode selectJumpByParamter(@Param("jump")Long jump, @Param("jump1")Long jump1,@Param("flag")int flag);

    /**
     * hgl
     * 通过businessId查询到开场白节点的记录
     * @param businessId
     * @return
     */
    List<WorkflowNode> selectNameByBusinessId(Long businessId);
}