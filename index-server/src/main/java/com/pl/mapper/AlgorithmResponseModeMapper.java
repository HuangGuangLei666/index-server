package com.pl.mapper;

import com.pl.model.AlgorithmResponseMode;
import org.apache.ibatis.annotations.Param;

public interface AlgorithmResponseModeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AlgorithmResponseMode record);

    int insertSelective(AlgorithmResponseMode record);

    AlgorithmResponseMode selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AlgorithmResponseMode record);

    int updateByPrimaryKeyWithBLOBs(AlgorithmResponseMode record);

    int updateByPrimaryKey(AlgorithmResponseMode record);

    AlgorithmResponseMode selectByCompanyIdAndBusinessIdAndOriginalId(@Param("companyId") Long companyId,
                                                                      @Param("businessId") Long businessId,
                                                                      @Param("originalId") Long originalId);

    int deleteByCompanyIdAndBusinessIdAndOriginalId(@Param("companyId") Long companyId,
                                                    @Param("businessId") Long businessId,
                                                    @Param("originalId") Long originalId);
}