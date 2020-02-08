package com.pl.indexserver.service;

import com.pl.indexserver.model.TCallAgentSelectDto;
import com.pl.model.TCallAgent;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TCallAgentService {
    int deleteByPrimaryKey(Long id);

    int insert(TCallAgent record);

    int insertSelective(TCallAgent record);

    TCallAgent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TCallAgent record);
    
    int batchUpdateCallAgentUsed(List<TCallAgent> callAgentList);

    int updateCallAgentUsed(TCallAgent callAgent);

    int updateByPrimaryKey(TCallAgent record);

    List<TCallAgent> getTCallAgentListByCompany_id(Long company_id);

    List<TCallAgent> getTCAllAgentListByupdate(@Param("company_id") Long company_id,
                                               @Param("autoAgentType") Integer autoAgentType,
                                               @Param("autoAgentNum") Integer autoAgentNum);

    List<TCallAgentSelectDto> selectTCallAgentSelect(Map<String, Object> map);


    /**
     * 统计公司拥有的坐席数量
     *
     * @param companyId 公司id
     * @return 返回统计结果
     * @throws Exception
     */
    int countByCompanyId(Long companyId) throws Exception;

    List<TCallAgent> selectByIdArray(String[] ids);

    /**
     * 根据任务id和状态查询数量
     * @param taskId
     * @param Status
     * @return
     */
    int countByTaskIdAndStatus(Long taskId, Long Status);

    List<TCallAgent> queryCallinList(Long companyId);

    List<TCallAgent> queryCallInListByCallInId(Long callInId);

    int selectCountOutnumber(String outNumber);

    TCallAgent getCallInNumber();

    int updateByCompanyAndUsedtaskId(TCallAgent tCallAgent);
}
