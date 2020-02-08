package com.pl.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.pl.indexserver.model.CallTaskIndexDto;
import com.pl.indexserver.model.DialogInDetail;
import com.pl.indexserver.model.DialogModelDto;
import com.pl.indexserver.model.PersonDto;
import com.pl.indexserver.query.TDialogQuery;
import com.pl.model.TCallAgentSelect;
import com.pl.model.TDialog;
import com.pl.model.TDialogCount;
import com.pl.model.TDialogSelect;

import com.pl.model.wx.DialogDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TDialogMapper {

    TDialog selectByPrimaryKey(@Param("id") Long id,@Param("postfix") String postfix);

    List<TDialog> getTDialogListByMap(@Param("company_id") Long company_id,
                                      @Param("task_id") Long task_id,@Param("postfix") String postfix);

    List<TDialog> getTDialogListToStat(@Param("company_id") Long company_id,
            @Param("task_id") Long task_id,@Param("postfix") String postfix);

    
     	     
    
    void addDialog(@Param("dialogList") List<TDialog> dialogList,@Param("postfix") String postfix);

    void addDialog(List<TDialog> dialogList);

    List<TDialogCount> selectAllTDialogStatusById(@Param("company_id")Long company_id,@Param("task_id") Long task_id,@Param("postfix") String postfix);

    List<TDialogCount> selectAllTDialogIntentionById(@Param("company_id")Long company_id,@Param("task_id") Long task_id,@Param("postfix") String postfix);

    List<TDialogSelect> selectAllTDialogTDialogModelDto(Pagination pagination,Map<String, Object> map);

    List<TDialogSelect> selectCallInTDialogList(Pagination pagination,Map<String, Object> map);

    //用于导出List
    List<TDialogSelect> selectAllTDialogTDialogModelDto(Map<String, Object> map);

    //根据任务ID查询未拨打的电话号码 , 并加入到REDIS
    List<TDialog> selectByTaskId(@Param("taskId") Long taskId,@Param("postfix") String postfix);


    //根据手机号码和任务ID修改状态
    int updateStatusByPhone(@Param("taskId") Long taskId,@Param("phone") String phone,
                            @Param("status") Integer status,@Param("agentId") Long agentId,@Param("postfix") String postfix);

    //根据AGENTID和任务ID修改状态
    int updateStatusByAgent(@Param("taskId") Long taskId,@Param("agentId") Long agentId,@Param("status") Integer status,@Param("postfix") String postfix);

    /**
     * 获取任务中的号码 用于判断导入时是否有相同
     * @param company_id 公司ID
     * @param task_id 任务ID
     * @return
     */
    List<String> getPhoeList(@Param("company_id")Long company_id,@Param("task_id")Long task_id,@Param("postfix") String postfix);

    /**
     * 根据传入的任务id和电话号码查询对应的通话记录数据
     *
     * @param taskId 对应会话id
     * @param telephone   电话号码
     * @return 返会查询结果
     */
    TDialog selectByCallTaskIdAndTelephone(@Param("taskId")String taskId, @Param("telephone")String telephone,@Param("postfix") String postfix);

    TDialog getDialogbyPhone(@Param("task_id") Long task_id,@Param("telephone") String telephone,@Param("postfix") String postfix);

    int updateDialogStatus(@Param("task_id") Long task_id,
                           @Param("telephone") String telephone,
                           @Param("status") Integer status,
                           @Param("agent_id") Long agent_id,
                           @Param("postfix") String postfix);


    int updateTDialog(TDialog tDialog);

    List<TCallAgentSelect> selectTCallAgentSelect(Map<String,Object> map);
    /**
     * 根据公司id和任务id查询有意向的客户（用于接口/callTask/task/queryStat，该接口实现后期需处理）
     *
     * @param companyId 公司id
     * @param taskId    任务id
     * @param postfix    表名后缀
     * @return 返回一个封装状态码和查询结果的对象
     * @throws Exception
     */
    Long selectAllIsIntentionByCompanyIdAndTaskId(@Param("companyId")Long companyId,@Param("taskId")Long taskId,
                                                  @Param("postfix") String postfix,@Param("type") String type);

    int countDialogByTaskId(@Param("companyId")Long companyId,@Param("taskId")Long taskId,@Param("postfix") String postfix);
    int countFinishTodayDialogByTaskId(@Param("companyId")Long companyId,@Param("taskId")Long taskId,@Param("postfix") String postfix);
    int countFinishAllDialogByTaskId(@Param("companyId")Long companyId,@Param("taskId")Long taskId,@Param("postfix") String postfix);
 
    
    Long callTimeSumByCompanyId(@Param("postfix") String postfix,@Param("companyId")Long companyId);

    List<CallTaskIndexDto> getCallRecordsOnlineByCompanyId(@Param("postfix") String postfix,@Param("companyId")Long companyId,@Param("starTime")Date starTime,@Param("endTime")Date endTime);

    /**
     * 根据用户id查看要需要拨打的电话数量
     * @param taskIds
     * @return
     */
    int countUndoTelephone(@Param("taskIds")List<Long> taskIds,@Param("months") List<String> months);

    int selectNoIntentionByCompanyIdAndTaskId(@Param("companyId")Long companyId,@Param("taskId")Long taskId,
                                              @Param("postfix") String postfix,@Param("type") String type);

    /**
     * 根据条件查询通话号码
     *
     * @param tDialogQuery 查询对象
     * @return
     * @throws Exception
     */
    List<TDialog> selectByQuery(TDialogQuery tDialogQuery);

    List<TDialog> selectCallInByQuery(TDialogQuery tDialogQuery);

    int selectReportFamilyPlaningCountByStatus(@Param("companyId")Long companyId,@Param("taskId")Long taskId,
                                               @Param("postfix") String postfix,@Param("status") String status);

    int selectReportJiangxiFamilyPlaningCountByStatus(@Param("companyId")Long companyId,@Param("taskId")Long taskId,
                                                      @Param("postfix") String postfix,@Param("status") String status);

    int selectReportSpeechcraftStatisticsCountByStatus(@Param("companyId")Long companyId,@Param("taskId")Long taskId,
                                                       @Param("postfix") String postfix,@Param("status") String status);

    TDialog selectDialogInByPrimaryKey(long dialogId);

    List<TDialogSelect> selectAllTDialogInTDialogModelDto(Map<String, Object> map);

    void updateDialogPriority(@Param("id")Long id,@Param("postfix") String postfix);

    List<TDialog> selectDialogByTaskId(String phonenumber);

    List<TDialog> selectAnswerListByUserId(Integer userId);

    int delAnswerListById(Long dialogId);

    List<TDialog> selectDialogPageByTaskId(Pagination pagination, @Param("phonenumber")String phonenumber);

    void updateDialogIsReadStatus(Long dialogId);

    List<TDialog> selectByTaskIdOfToday(@Param("phonenumber")String phonenumber, @Param("format")String format);
}