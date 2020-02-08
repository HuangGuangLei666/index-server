package com.pl.indexserver.service;

import com.pl.indexserver.model.CraftConfigDto;
import com.pl.indexserver.model.QCommonCraftDto;
import com.pl.indexserver.model.redisdto.AlgorithmDataSynDto;
import com.pl.model.QCommonCraft;
import com.pl.model.TmUser;

import java.util.List;
import java.util.Map;

/**
 * 话术相关服务类
 */
public interface QCommonCraftService {

    /**
     * 根据公司id和智库id查询相关通用话术
     *
     * @param companyId 对应公司id
     * @param businessId 对应智库id
     * @return 返回查询到结果集
     */
    List<QCommonCraftDto> selectByCompanyIdAndBusinessId(Long companyId, Long businessId) throws Exception;

    /**
     * 根据id查询相关通用话术详情
     *
     * @param id 主键id
     * @return 返回查询到的数据
     */
    QCommonCraftDto selectDetailByPrimaryKey(Long id) throws Exception;

    /**
     * 根据主键id删除对应的通用话术
     *
     * @param id 对应id
     */
    Boolean deleteDetailByPrimaryKey(Long id) throws Exception;

    /**
     * 根据主键id查找对应的通用话术
     *
     * @param id 对应id
     * @return
     */
    QCommonCraft selectByPrimaryKey(Long id) throws Exception;

    QCommonCraft selectQCommonCraftByCraftIdAndCompanyIdAndBusinessId(String craftId, Long companyId, Long businessId) throws Exception;

    /**
     * 新增一条通用话术
     *
     * @param qCommonCraftDto 通用话术数据对象
     * @return
     */
    Boolean insert(QCommonCraftDto qCommonCraftDto) throws Exception;

    /**
     * 修改对应的通用话术数据
     *
     * @param qCommonCraftDto 通用话术数据对象
     * @return
     */
    Boolean updateByPrimaryKey(QCommonCraftDto qCommonCraftDto) throws Exception;

    /**
     * 根据主键id查询对应的文件信息
     *
     * @param id 主键id
     * @return 返回查询到的结果;filePath:String,fileNames:String[]
     */
    Map<String, Object> selectFileDetailByPrimaryKey(Long id) throws Exception;

    /**
     * 查询通用话术配置信息
     *
     * @param companyId
     * @param businessId
     * @return
     */
    CraftConfigDto selectCommonCraftConfig(Long companyId, Long businessId);

    /**
     * 修改通用话术配置信息
     *
     * @param craftConfig
     * @return
     */
    Boolean updateCommonCraftConfig(TmUser user, CraftConfigDto craftConfig);

    /**
     * 克隆智库下的话术
     *
     * @param user
     * @param sourceCompanyId
     * @param sourceBusinessId
     * @param targetCompanyId
     * @param targetBusinessId
     * @return
     */
    void clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId,Map<Long, Long> flowIdMap) throws Exception;

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
