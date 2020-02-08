package com.pl.mapper;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.pl.indexserver.model.CallTaskStatDto;
import com.pl.model.CallTaskStat;
import com.pl.model.Statistice;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CallTaskStatMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CallTaskStat record);

    int insertSelective(CallTaskStat record);

    CallTaskStat selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CallTaskStat record);

    int updateByPrimaryKey(CallTaskStat record);

    /**
     * 根据起始时间统计相应基础数据
     *
     * @return 返回查询到的数据
     */
    CallTaskStatDto queryBasicStat(Map<String, Object> params);

    /**
     * 根据起始时间统计相应任务数据
     *
     * @return 返回查询到的数据
     */
    List<CallTaskStatDto> queryTaskStat(Map<String, Object> params);


    /**
     * 查询所有任务；
     *
     * @param companyId 公司id(若为空则查询所有公司的任务)
     *
     * @return 返回任务id与任务名字的键值对；
     */
    List<Statistice> queryAll(@Param("companyId") Long companyId);


    /**
     * 查询意向客户（整个公司）
     *
     * @param map 条件集合
     */

    List<Statistice> selectMapStatisticeForIntention(Pagination pagination, Map<String, Object> map);

    //导出list查询
    List<Statistice> selectMapStatisticeForIntention(Map<String, Object> map);

    /**
     * 根据相关信息根据时间段统计意向客户和接听量
     *
     * @param params 包含公司id，业务id，起始时间，终止时间的参数集
     * @return 返回统计结果
     */
    List<CallTaskStatDto> queryStatistics(Map<String, Object> params);

}