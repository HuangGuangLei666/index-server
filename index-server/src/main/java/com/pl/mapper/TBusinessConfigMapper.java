package com.pl.mapper;

import com.pl.model.SysBusinessTemplate;
import com.pl.model.TBusinessConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TBusinessConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TBusinessConfig record);

    int insertSelective(TBusinessConfig record);

    TBusinessConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TBusinessConfig record);

    int updateByPrimaryKey(TBusinessConfig record);

    List<TBusinessConfig> selectByCompanyIdBusinessId(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    TBusinessConfig selectByBusinessIdAndConfigType(@Param("businessId") Long businessId, @Param("configType") String configType);

    List<SysBusinessTemplate> getSysBuinessTemplates();
}