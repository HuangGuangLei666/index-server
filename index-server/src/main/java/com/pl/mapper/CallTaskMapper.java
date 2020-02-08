package com.pl.mapper;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.pl.indexserver.model.CallTaskIndexDto;
import com.pl.model.CallTask;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CallTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CallTask record);

    int insertSelective(CallTask record);

    CallTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CallTask record);

    int updateOrInsertByPrimaryKeySelective(CallTask record);

    int updateByPrimaryKeyWithBLOBs(CallTask record);

    int updateByPrimaryKey(CallTask record);

    List<CallTask> getCallTaskListByCompany_id(@Param("company_id") Long company_id,
                                               @Param("taskName") String taskName);

    List<CallTask> getCallTaskListByCompany_idOutTaskid(@Param("company_id") Long company_id,
                                                        @Param("taskName") String taskName,
                                                        @Param("taskId") Long taskId);

    List<CallTask> getCallTaskListByMap(Pagination pagination,
                                        @Param("uid") String uid,
                                        @Param("company_id") Long company_id,
                                        @Param("task_state") Integer taskState,
                                        @Param("taskName") String taskName);

    CallTask getCallTaskByid(@Param("id") Long id, @Param("company_id") Long company_id);

    int updateTask_state(@Param("pause") Integer pause,
                         @Param("id") Long id,
                         @Param("modify_date") Date modify_date,
                         @Param("begindate") Date begindate,
                         @Param("enddate") Date enddate,
                         @Param("timeQuantum") String timeQuantum,
                         @Param("totalNumber") Integer totalNumber,
                         @Param("additionalInfo") String additionalInfo);

    int updateEnd(@Param("id") Long id, @Param("modify_date") Date modify_date);

    // 任务结束，修改坐席状态
    int updateAgentStatus(@Param("id") Long id);

    //查询当前任务中是否有未拨打
    int selectNotDialing(@Param("company_id") Long company_id,
                         @Param("taskid") Long taskid);
    //=================================================

    //加载所有已经发布，但没有结束的任务
    List<CallTask> selectAllAvailable();

    /**
     * 获取车牌号
     *
     * @param task_id
     * @return
     */
    String getCar_Numbers(@Param("task_id") Long task_id, @Param("ct_phone") String ct_phone);

    /**
     * 根据业务idc查询外呼任务id
     *
     * @param businessId 业务id
     * @return 返回查询到的结果集
     */
    @Select("select id from call_task where business_id = #{businessId}")
    List<Long> selectTaskIdByBusinessId(Long businessId);

    /**
     * 根据业务id查询相应的任务id
     *
     * @param businessId 业务id
     * @return 返回查询到的结果集
     */
    @Select("select id from call_task where business_id = #{businessId}")
    List<Long> selectIdByBusinessId(Long businessId);

    /**
     * 首页外呼任务数据。
     * @param companyId
     * @param uid
     * @param date
     * @return
     */
    Map<String,Integer> getIndexCallData(@Param("companyId")Long companyId, @Param("uid")String uid,
                                         @Param("endTime")Date date);

    List<CallTaskIndexDto> getCallTaskSum(@Param("companyId")Long companyId,@Param("starTime")Date starTime,@Param("endTime")Date endTime);

    List<CallTask> getCallTaskBystatus(@Param("userId")String userId,@Param("status") int status);

    List<CallTask> getCallTaskByCompanyId(Long companyId);

    List<CallTask> getCallTaskByCompanyIdAndUsername(String userName);
}