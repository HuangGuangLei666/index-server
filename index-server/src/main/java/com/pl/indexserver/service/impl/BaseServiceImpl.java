package com.pl.indexserver.service.impl;

import com.pl.indexserver.query.BaseQuery;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.service.BaseService;
import com.pl.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BaseServiceImpl<T> implements BaseService<T> {

    @Autowired
    private BaseMapper<T> baseMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public boolean deleteByPrimaryKey(String primaryKey) throws Exception {
        return baseMapper.deleteByPrimaryKey(primaryKey)>0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public boolean insert(T t) throws Exception {
        return baseMapper.insert(t)>0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public boolean insertSelective(T t) throws Exception {
        return baseMapper.insertSelective(t)>0;
    }

    @Override
    public T selectByPrimaryKey(String primaryKey) throws Exception {
        return baseMapper.selectByPrimaryKey(primaryKey);
    }

    @Override
    public List<T> selectAll() throws Exception {
        return baseMapper.selectAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public boolean updateByPrimaryKey(T t) throws Exception {
        return baseMapper.updateByPrimaryKey(t)>0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public boolean updateByPrimaryKeySelective(T t) throws Exception {
        return baseMapper.updateByPrimaryKeySelective(t)>0;
    }

    @Override
    public Long countByQuery(BaseQuery baseQuery) throws Exception {
        return baseMapper.countByQuery(baseQuery);
    }

    @Override
    public Page<T> selectByQuery(BaseQuery baseQuery) throws Exception {
        Long count = countByQuery(baseQuery);
        List<T> list = baseMapper.selectByQuery(baseQuery);
        return new Page<T>(count,list);
    }
}
