package com.pl.mapper;

import com.pl.model.AlgorithmQCommonCraft;
import org.apache.ibatis.annotations.Param;

public interface AlgorithmQCommonCraftMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AlgorithmQCommonCraft record);

    int insertSelective(AlgorithmQCommonCraft record);

    AlgorithmQCommonCraft selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AlgorithmQCommonCraft record);

    int updateByPrimaryKeyWithBLOBs(AlgorithmQCommonCraft record);

    int updateByPrimaryKey(AlgorithmQCommonCraft record);

    AlgorithmQCommonCraft selectByCompanyIdAndBusinessIdAndOriginalId(@Param("companyId") Long companyId,
                                                                      @Param("businessId") Long businessId,
                                                                      @Param("originalId") Long originalId);

    int deleteByCompanyIdAndBusinessIdAndOriginalId(@Param("companyId") Long companyId,
                                                    @Param("businessId") Long businessId,
                                                    @Param("originalId") Long originalId);
}