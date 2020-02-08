package com.pl.mapper;

import com.pl.indexserver.model.CommonCraftConfigDto;
import com.pl.indexserver.model.CraftConfigDto;
import com.pl.indexserver.model.QCommonCraftDto;
import com.pl.model.QCommonCraft;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface QCommonCraftMapper {
    int deleteByPrimaryKey(Long id);

    int insert(QCommonCraft record);

    int insertSelective(QCommonCraft record);

    QCommonCraft selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QCommonCraft record);

    int updateByPrimaryKeyWithBLOBs(QCommonCraft record);

    int updateByPrimaryKey(QCommonCraft record);

    /**
     * 根据公司id查询其对应的通用话术
     *
     * @param companyId 对应的公司id
     * @param businessId 对应的智库id
     * @return 返回查询到的结果集
     */
    List<QCommonCraftDto> selectByCompanyIdAndBusinessId(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    /**
     * 根据公司id查询其对应的通用话术
     *
     * @param companyId 对应的公司id
     * @param businessId 对应的智库id
     * @return 返回查询到的结果集
     */
    List<QCommonCraft> selectByParams(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    /**
     * 根据主键id查询其对应的通用话术详情
     *
     * @param id 主键id
     * @return 返回查询到的结果集
     */
    QCommonCraftDto selectDetailByPrimaryKey(Long id);


    /**
     * 根据话术表示查找对应的通用话术
     *
     * @param craftId 话术标识
     * @return
     */
    QCommonCraft selectByCraftIdAndCompanyIdAndBusinessId(@Param("craftId") String craftId, @Param("companyId") Long companyId, @Param("businessId") Long businessId);

    /**
     * 根据主键id删除相关通用话术问答数据
     *
     * @param id 主键id
     * @return
     */
    int deleteDetailByPrimaryKey(Long id);

    /**
     * 根据公司id 智库id查询配置信息
     *
     * @return
     */
    CraftConfigDto selectConfigByCompanyIdAndBusinessId(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    /**
     * 根据公司id 智库id查询通用配置信息
     *
     * @param companyId
     * @param businessId
     * @return
     */
    List<CommonCraftConfigDto> selectCommonConfigByCompanyIdAndBusinessId(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    /**
     * 插入通用配置
     *
     * @param companyId
     * @param businessId
     * @param flag
     * @param ruleType
     * @param userId
     * @param createDate
     * @param craftId
     * @return
     */
    int insertCommonConfig(@Param("companyId") Long companyId, @Param("businessId") Long businessId,
                           @Param("flag") Integer flag, @Param("ruleType") Integer ruleType, @Param("userId") String userId,
                           @Param("createDate") Date createDate, @Param("craftId") Long craftId);

    /**
     * 修改通用配置
     *
     * @param companyId
     * @param businessId
     * @param flag
     * @param craftId
     * @return
     */
    int updateCommonConfig(@Param("companyId") Long companyId, @Param("businessId") Long businessId,
                           @Param("flag") Integer flag, @Param("craftId") String craftId, @Param("status") Integer status);

    /**
     * 根据公司标识,智库标识和话术标识获取通用话术
     * @param companyId 公司标识
     * @param craftId   话术标识
     * @param businessId  智库标识
     * @return
     */
    QCommonCraft selectByCompanyIdAndCraftId(@Param("companyId")Long companyId,@Param("craftId")String craftId,@Param("businessId")Long businessId);

    /**
     * 导出通用话术
     * @param map
     * @return
     */
    List<QCommonCraft> qcommon(Map<String, Object> map);
}