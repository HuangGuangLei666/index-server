package com.pl.indexserver.service;

import com.pl.indexserver.model.ResponseModeDto;
import com.pl.indexserver.model.redisdto.AlgorithmDataSynDto;

import java.util.List;
import java.util.Map;

/**
 * 响应方式相关服务类
 */
public interface ResponseModeService {

    /**
     * 根据公司id查询其相关相应方式
     *
     * @param companuId 对应公司id
     * @return 返回查询到结果集
     */
    List<ResponseModeDto> selectByCompanyId(String companuId) throws Exception;

    /**
     * 根据指定主键id查询对应响应方式数据
     *
     * @param id 指定主键id
     * @return 返回查询到结果
     */
    ResponseModeDto selectByPrimaryKey(String id) throws Exception;

    /**
     * 根据主键id删除对应的通用话术
     *
     * @param id 对应id
     * @return 返回删除掉的行数
     */
    int deleteByPrimaryKey(String id) throws Exception;

    /**
     * 修改对应的响应方式对象数据
     *
     * @param responseModeDto 响应方式数据对象
     * @return  返回修改掉的行数
     */
    int updateByPrimaryKeySelective(ResponseModeDto responseModeDto) throws Exception;

    /**
     * 新增一条响应方式
     *
     * @param responseModeDto 响应方式数据对象
     * @return  返回新增数据的行数
     */
    int insert(ResponseModeDto responseModeDto) throws Exception;

    /**
     * 根据响应方式id查询是否流程绑定
     *
     * @param id 响应方式对象id
     * @return  返回true表示该响应方式被绑定
     */
    Boolean selectWorkFlowByPrimaryKey(String id) throws Exception;

    /**
     * 克隆智库下所有响应方式
     *
     * @param sourceCompanyId
     * @param sourceBusinessId
     * @param targetCompanyId
     * @param targetBusinessId
     *
     * @return
     */
    Map<Long,Long> clone(Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId);

    /**
     * 推送算法同步数据到队列
     *
     * @param operation
     * @param id
     * @param companyId
     * @param businessId
     */
    void pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum operation, Long id, Long companyId, Long businessId);
}
