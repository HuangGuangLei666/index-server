package com.pl.indexserver.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pl.indexserver.model.CallTaskIndexDto;
import com.pl.indexserver.model.CallTaskMsg;
import com.pl.model.CallTask;
import com.pl.model.TCallAgent;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CallTaskService {
    Page<CallTaskMsg> getIndex(String uid, Long company_id, Integer taskState, String taskName,
                               int pageIndex, int pageSize);

    int deleteByPrimaryKey(Long id);

    int insert(CallTask record);

    int insertSelective(CallTask record);

    CallTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CallTask record);
    
    int updateCallTaskStatus(CallTask record, List<TCallAgent> callAgentList);

    int updateOrInsertByPrimaryKeySelective(CallTask record);

    int updateByPrimaryKeyWithBLOBs(CallTask record);

    int updateByPrimaryKey(CallTask record);

    List<CallTask> getCallTaskListByCompany_id(Long company_id, String taskName);

    List<CallTask> getCallTaskListByCompany_id2(Long company_id, String taskName, Long task_id);

    List<CallTask> getCallTaskListByMap(String uid, Long company_id, Integer taskState, String taskid);

    CallTask getCallTaskByid(Long id, Long company_id);

    int updateTask_state(Integer pause, Long id, Date modify_date,
                         Date begindate, Date enddate, String timeQuantum, Integer totalNumber,String additionalInfo);

    int updateEnd(Long id,Date modify_date);

    int updateAgentStatus(Long id);

    //查询当前任务中是否有未拨打
    int selectNotDialing(Long company_id , Long taskid);

    /**
     * 获取车牌号
     * @param task_id
     * @return
     */
    String getCar_Numbers(Long task_id,String ct_phone);

    /**
     * 首页外呼统计
     * @param companyId
     * @param uid
     * @param endTime
     * @return
     */
    Map<String,Integer> getSum_CallTaskAndCalls(Long companyId, String uid,Date endTime);

    List<CallTaskIndexDto> getCallTaskCount(Long companyId,Date starTime,Date endTime);

    List<CallTask> getCallTaskBystatus(String userId,int status);

    boolean stopCallTask(Long taskId) throws Exception;

    List<CallTask> getCallTaskByCompanyId(Long companyId);
}
