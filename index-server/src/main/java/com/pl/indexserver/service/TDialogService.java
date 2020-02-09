package com.pl.indexserver.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.model.TDialogModelDto;
import com.pl.indexserver.query.TDialogQuery;
import com.pl.model.*;
import com.pl.model.wx.DialogDto;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface TDialogService {

    TDialog selectByPrimaryKey(Long taskId, Long id);

    List<TDialog> getTDialogListByMap(Long company_id, Long task_id);
    List<TDialog> getTDialogListToStat(Long company_id, Long task_id);

    void addDialog(List<TDialog> dialogList, Long taskId);

    List<TDialogCount> selectAllTDialogIntentionById(Long company_id, Long task_id);

    List<TDialogCount> selectAllTDialogStatusById(Long company_id, Long task_id);

    Page<TDialogModelDto> selectAllTDialogTDialogModelDto(int pageIndex, int pageNum, Map<String, Object> map);

    Page<TDialogModelDto> selectCallInTDialogList(int pageIndex, int pageNum, Map<String, Object> map);

    void exportAllTDialogTDialogModelDto(Map<String, Object> map, HttpServletResponse response);

    void exportAllTDialogInTDialogModelDto(Map<String, Object> map, HttpServletResponse response);

    List<String> getCallTaskPhoeList(Long company_id, Long task_id);

    /**
     * 根据传入的任务id和电话号码查询对应的通话记录数据
     *
     * @param callTtaskId 对应会话id
     * @param telephone   电话号码
     * @return 返会查询结果
     */
    TDialog selectByTaskIdAndTelephone(Long callTtaskId, String telephone) throws Exception;

    TDialog getDialogbyPhone(Long task_id, String telephone);

    int updateDialogStatus(Long task_id, String telephone, Integer status, Long agent_id);

    int updateTDialog(TDialog tDialog, Long taskId);

    TDialog getDialogbyId(Long task_id, long dialogId);

    TDialog getDialogInbyId(long dialogId);

    List<TCallAgentSelect> selectTCallAgentSelect(Map<String, Object> map);

    /**
     * 根据公司id和任务id查询有意向的客户（用于接口/callTask/task/queryStat，该接口实现后期需处理）
     *
     * @param companyId 公司id
     * @param taskId    任务id
     * @return 返回一个封装状态码和查询结果的对象
     * @throws Exception
     */
    TDialogCount selectAllIsIntentionByCompanyIdAndTaskId(Long companyId, Long taskId, String type);

    /**
     * 根据不同的评分方式获取无意向的数量
     *
     * @param companyId
     * @param taskId
     * @param type
     * @return
     * @throws Exception
     */
    TDialogCount selectNoIntentionByCompanyIdAndTaskId(Long companyId, Long taskId, String type) throws Exception;

    int countDialogByTaskId(Long companyId, Long taskId);
    int countFinishTodayDialogByTaskId(Long companyId, Long taskId);
    int countFinishAllDialogByTaskId(Long companyId, Long taskId);

    int countUndoTelephone(List<Long> taskIds, List<String> months);

    String getTablePostfix(long taskId);

    /**
     * 根据条件查询通话号码
     *
     * @param tDialogQuery 查询对象
     * @return
     * @throws Exception
     */
    List<TDialog> selectByQuery(TDialogQuery tDialogQuery) throws Exception;

    List<TDialog> selectCallInByQuery(TDialogQuery tDialogQuery) throws Exception;

    int getVisitSuccessReportFamilyPlaningCount(Long companyId, Long taskId);

    int getVisitSuccessReportJiangxiFamilyPlaningCount(Long companyId, Long taskId);

    int getVisitSuccessReportSpeechcraftStatisticsCount(Long companyId, Long taskId);

    ChatPhone getPhoneByUserName(String userName);

    List<TDialog> selectByTaskId(String phonenumber);

    List<TDialog> selectAnswerListByUserId(Integer userId);

    int delAnswerListById(Long dialogId);

    Page<DialogDto> getDialogListOfPage(int pageIndex, int pageNum, Integer userId);

    void updateDialogIsReadStatus(Long dialogId);

    List<TDialog> selectByTaskIdOfToday(String phonenumber, String format);

    Page<DialogDto> getBindingDialogListOfPage(int pageIndex, int pageNum, String openid);

    Page<DialogDto> getBdingDialogListOfPage(int pageIndex, int pageNum, Integer userId);

    Page<DialogDto> getAnswerListPageQry(int pageIndex, int pageNum, Integer userId);

}
