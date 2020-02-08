package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TmCustomerService;
import com.pl.mapper.TmCustomerMapper;
import com.pl.model.TmCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TmCustomerServiceImpl implements TmCustomerService {
    @Autowired
    private TmCustomerMapper tmCustomerMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return tmCustomerMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(TmCustomer record) {
        return tmCustomerMapper.insert(record);
    }

    @Override
    public int insertSelective(TmCustomer record) {
        return tmCustomerMapper.insertSelective(record);
    }

    @Override
    public TmCustomer selectByPrimaryKey(Long id) {
        return tmCustomerMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(TmCustomer record) {
        return tmCustomerMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(TmCustomer record) {
        return tmCustomerMapper.updateByPrimaryKey(record);
    }

    @Override
    public void addCustomer(Set<TmCustomer> set) {
        tmCustomerMapper.addCustomer(set);
    }

    @Override
    public int deleteTmCustomerByCtType(String ct_type) {
        return tmCustomerMapper.deleteTmCustomerByCtType(ct_type);
    }

    @Override
    public List<TmCustomer> getTmCustomerList(Long task_id) {
        return tmCustomerMapper.getTmCustomerList(task_id);
    }

    @Override
    public TmCustomer selectByTelephoneAndCompanyId(String telephone, String companyId) throws Exception {
        return tmCustomerMapper.selectByTelephoneAndCompanyId(telephone,companyId);
    }

    @Override
    public List<String> selectByCompanyId(String companyId) {
        return tmCustomerMapper.selectByCompanyId(companyId);
    }

    @Override
    public void addCustonerBatch(Set<TmCustomer> set) {
        tmCustomerMapper.addCustonerBatch(set);
    }

    @Override
    public List<TmCustomer> getTelephonesFromPrivatCustomer(Long companyId) throws Exception {
        return tmCustomerMapper.getTelephonesFromPrivatCustomer(companyId);
    }
}
