package com.pl.mapper;

import com.pl.indexserver.model.BusinessFocusDto;
import com.pl.model.TBusinessFocus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TBusinessFocusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TBusinessFocus record);

    int insertSelective(TBusinessFocus record);

    TBusinessFocus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TBusinessFocus record);

    int updateByPrimaryKey(TBusinessFocus record);

    List<BusinessFocusDto> selectBusinessFocusDtoByName(@Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("name") String name);

    List<BusinessFocusDto> selectBusinessFocusDtoByNames(@Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("names") String[] names);

    String selectBusinessFocusNamesDtoByIds(@Param("companyId") Long companyId, @Param("businessId") Long businessId, @Param("ids") String ids);

    List<TBusinessFocus> selectByCompanyIdAndBusinessId(@Param("companyId") Long companyId, @Param("businessId") Long businessId);
}