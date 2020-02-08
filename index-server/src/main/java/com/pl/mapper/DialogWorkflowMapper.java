package com.pl.mapper;

import com.pl.model.DialogWorkflow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DialogWorkflowMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DialogWorkflow record);

    int insertSelective(DialogWorkflow record);

    DialogWorkflow selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DialogWorkflow record);

    int updateByPrimaryKeyWithBLOBs(DialogWorkflow record);

    int updateByPrimaryKey(DialogWorkflow record);

    DialogWorkflow getDialogWorkflowByBusiness_id(@Param("company_id") Long company_id, @Param("business_id") Long business_id);

    DialogWorkflow getDialogWorkflowByFlowId(@Param("companyId") Long companyId, @Param("flowId") Long flowId);

    List<DialogWorkflow> getDialogWorkFlowList(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    List<DialogWorkflow> getDialogWorkFlowDetilList(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    DialogWorkflow getPrimaryDiaLogWorkFlow(@Param("businessId") Long businessId);

    int deleteByFlowIdAndBusinessId(@Param("flowId") Long flowId, @Param("businessId") Long businessId);

    int selectDialogWorkflowMaxSort(@Param("parentId") Long parentId);

    int selectDialogWorkFlowCountByParentId(@Param("businessId") Long businessId, @Param("parentId") Long parentId);

    DialogWorkflow getFirstWorkNode(@Param("businessId") Long businessId);
}