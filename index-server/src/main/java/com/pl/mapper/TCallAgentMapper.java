package com.pl.mapper;

import com.pl.model.TCallAgent;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TCallAgentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TCallAgent record);

    int insertSelective(TCallAgent record);

    TCallAgent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TCallAgent record);

    int updateByPrimaryKey(TCallAgent record);
    
    int  batchUpdateCallAgentUsed(@Param("callAgentList") List<TCallAgent> callAgentList);

    int updateCallAgentUsed(TCallAgent callAgent);

    List<TCallAgent> getTCallAgentListByCompany_id(Long company_id);

    List<TCallAgent> getTCAllAgentListByupdate(@Param("company_id")Long company_id,
                                               @Param("autoAgentType") Integer autoAgentType,
                                               @Param("autoAgentNum") Integer autoAgentNum);


    List<TCallAgent> selectByTaskIds(List<Long> list);

    /**
     *  统计公司拥有的坐席数量
     * @param companyId 公司id
     * @return  返回统计结果
     */
    @Select("select count(id) from t_call_agent where company_id=#{companyId}")
    Integer countByCompanyId(Long companyId);

    /**
     * 根据任务查询占用的坐席数
     * @param taskId
     * @return
     */
    @Select("select count(id) from t_call_agent where used_taskid=#{taskId}")
    Integer countByTaskId(Long taskId);

    List<TCallAgent> selectByIdArray(@Param("ids")String[] ids);


    int getUsableAgentByCompanyId(@Param("company_id")Long company_id);

    int countByTaskIdAndStatus(@Param("taskId") Long taskId,@Param("status") Long status);

    List<TCallAgent> queryCallinList(Long companyId);

    List<TCallAgent> queryCallInListByCompanyId(Long companyId);

    List<TCallAgent> queryCallInListByCallInId(Long callInId);

    int selectCountOutnumber(String outNumber);

    TCallAgent getCallInNumber();

    int updateByCompanyAndUsedtaskId(TCallAgent tCallAgent);
}