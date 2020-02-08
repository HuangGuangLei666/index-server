package com.pl.indexserver.service;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 智库报表业务接口
 *
 * @Author bei.zhang
 * @Date 2018/9/13 11:11
 */
public interface BusinessReportService {

    /**
     * 获取回访成功数量
     *
     * @return
     */
    int getVisitSuccessCount(Long companyId, Long taskId);

    /**
     * 获取报表表名
     *
     * @param taskId
     * @return
     */
    String getReportTableName(Long taskId);

    /**
     * 验证智库是否启用统计配置
     *
     * @param businessId
     * @return
     */
    boolean checkBusinessStatistics(Long businessId);


    /**
     * 根据条件获取报表列表
     *
     * @param params
     * @return
     */
    void exportBusinessReport(Long taskId, Map<String, Object> params, HttpServletResponse response);

}
