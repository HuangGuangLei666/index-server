package com.pl.mapper;

import com.pl.model.CallTask;
import com.pl.model.TDialog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务
 */
public interface ScheduledMapper {
    /**
     * 获取所有的任务
     * @return
     */
    List<CallTask> getCallTaskList();

    /**
     * 查询所有的公司ID
     * @return
     */
    List<Integer> getCompany_Id();

    /**
     * 根据公司ID 查询所有的任务
     * @param company_id
     * @return
     */
    List<CallTask> getCallTaskListByCompany_id(@Param("company_id") Integer company_id);




}
