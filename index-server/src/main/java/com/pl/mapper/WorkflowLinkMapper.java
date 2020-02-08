package com.pl.mapper;

import com.pl.model.WorkflowLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface WorkflowLinkMapper {

    int deleteByPrimaryKey(Long id);

    int insert(WorkflowLink record);

    int insertSelective(WorkflowLink record);

    WorkflowLink selectByPrimaryKey(@Param("flowId")Long flowId,@Param("ruleId") Long id);

    int updateByPrimaryKeySelective(WorkflowLink record);

    int updateByPrimaryKey(WorkflowLink record);

    int getWorkFlowLinksByWorkNodeId(@Param("ruleId") Long paramter,@Param("flowId")Long flowId);

    int deleteWorkLinkByWorkFlowId(@Param("flowId")Long flowId,@Param("ruleId")Long ruleId);

    /**
     * 根据应答方式id查询被绑定的数量
     * @param responseId    应答方式id
     * @return  返回统计结果
     */
    int selectByResponseId(String responseId);

    int deleteByFlowId(@Param("flowId") Long flowId);

    /**
     * 根据公司标识和智库标识查询相应流程连接线数据
     * @param companyId 公司标识
     * @param businessId    智库标识
     * @return
     */
    List<WorkflowLink> selectByCompanyIdAndBusinessId(@Param("companyId")Long companyId, @Param("businessId") Long businessId);

    List<WorkflowLink> getWorkFlowLink(@Param("workNodeId") String workNodeId, @Param("workFlowId") Long workFlowId);

    /**
     * hgl
     * 获取当前节点下的线
     * @param workNodeId
     * @param workFlowId
     * @return
     */
    List<WorkflowLink> getWorkFlowLinkByFromId(@Param("workNodeId") String workNodeId, @Param("workFlowId") Long workFlowId);


}