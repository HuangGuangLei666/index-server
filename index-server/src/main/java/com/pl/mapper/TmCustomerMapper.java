package com.pl.mapper;

import com.pl.model.TmCustomer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface TmCustomerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TmCustomer record);

    int insertSelective(TmCustomer record);

    TmCustomer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TmCustomer record);

    int updateByPrimaryKey(TmCustomer record);

    void addCustomer(@Param("set") Set<TmCustomer> set);

    void addCustonerBatch(@Param("set") Set<TmCustomer> set);

    int deleteTmCustomerByCtType(@Param("ct_type") String ct_type);

    List<TmCustomer> getTmCustomerList(@Param("task_id") Long task_id);

    /**
     * 根据电话号码和公司id查询对应数据
     *
     * @param telephone 电话号码
     * @param companyId 公司id
     * @return 返回查询到的结果
     * @throws Exception
     */
    TmCustomer selectByTelephoneAndCompanyId(@Param("telephone") String telephone, @Param("companyId") String companyId);

    List<String> selectByCompanyId(@Param("companyId") String companyId);

    /**
     * 查询所有私人客户的联系号码
     *
     * @param companyId 公司标识
     * @return
     * @throws Exception
     */
    @Select("select ct_phone from tm_customer where company_id = #{companyId} and cm_type = 2")
    List<TmCustomer> getTelephonesFromPrivatCustomer(Long companyId);
}