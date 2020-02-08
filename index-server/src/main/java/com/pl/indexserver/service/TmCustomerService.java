package com.pl.indexserver.service;

import com.pl.model.TmCustomer;

import java.util.List;
import java.util.Set;

public interface TmCustomerService {

    int deleteByPrimaryKey(Long id);

    int insert(TmCustomer record);

    int insertSelective(TmCustomer record);

    TmCustomer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TmCustomer record);

    int updateByPrimaryKey(TmCustomer record);

    void addCustomer(Set<TmCustomer> set);

    void addCustonerBatch(Set<TmCustomer> set);

    int deleteTmCustomerByCtType(String ct_type);

    List<TmCustomer> getTmCustomerList(Long task_id);

    /**
     * 根据电话号码和公司id查询对应数据
     *
     * @param telephone 电话号码
     * @param companyId 公司id
     * @return 返回查询到的结果
     * @throws Exception
     */
    TmCustomer selectByTelephoneAndCompanyId(String telephone, String companyId) throws Exception;

    List<String> selectByCompanyId(String companyId);

    /**
     * 查询所有私人客户的联系号码
     *
     * @param companyId 公司标识
     * @return
     * @throws Exception
     */
    List<TmCustomer> getTelephonesFromPrivatCustomer(Long companyId) throws Exception;
}
