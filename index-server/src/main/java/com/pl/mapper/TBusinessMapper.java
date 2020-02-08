package com.pl.mapper;

import com.pl.indexserver.model.TBusinessModelDto;
import com.pl.model.TBusiness;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 业务表
 */
public interface TBusinessMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TBusiness record);

    int insertSelective(TBusiness record);

    TBusiness selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TBusiness record);

    int updateByPrimaryKey(TBusiness record);

    List<TBusiness> getTBusinessListByCompany_id(@Param("company_id") Long company_id);

    List<TBusinessModelDto> getTBusinessDescribeListByCompanyId(@Param("companyId") String companyId);

    List<TBusiness> selectByKeyList(List<Long> list);

    Integer countSmsProbably(Long bussinessId);

    @Select("update t_business set modify_date = now() where id = #{id}")
    Integer updateModifyDateById(Long id);

    TBusiness getBusinessName(Long businessId);
}