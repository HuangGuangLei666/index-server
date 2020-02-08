package com.pl.indexserver.service;

import com.pl.indexserver.model.CallTaskStatDto;
import com.pl.indexserver.model.StatisticeModelDto;
import com.pl.indexserver.query.BaseQuery;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.query.TCustmIntentionQuery;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface CallTaskStatService {

    /**
     * 根据起始时间统计基础信息
     *
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 返回查询到的结果集
     */
    CallTaskStatDto queryBasicStat(String beginDate, String endDate, String companyId, String type);

    /**
     * 根据起始时间统计任务信息
     *
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 返回查询到的结果集
     */
    List<CallTaskStatDto> queryTaskStat(String beginDate, String endDate, String companyId, String type);


    /**
     * 根据公司id查询相关任务
     *
     * @param companyId 公司id
     * @return 返回查询到的结果集
     */
    List<StatisticeModelDto> selectTaskByCompanyId(Long companyId);

    /**
     * 查询意向客户（整个公司）
     *
     * @param baseQuery 条件对象
     */

    Page<StatisticeModelDto> selectMapStatisticeForIntention(TCustmIntentionQuery baseQuery);

    void exportStatisticeForIntention(TCustmIntentionQuery baseQuery, HttpServletResponse response);

    /**
     * 根据业务id以及起始时间查询相关统计信息
     *
     * @param companyId 公司id
     * @param businessId 业务id
     * @return 返回意向客户和接听量的统计数据
     * @throws Exception
     */
    List<CallTaskStatDto> queryStatistics(Long companyId, Long businessId) throws Exception;

}
