package com.pl.mapper;

import com.pl.model.TAccSmsaccount;

public interface TAccSmsaccountMapper {
    int deleteByPrimaryKey(Long companyId);

    int insert(TAccSmsaccount record);

    int insertSelective(TAccSmsaccount record);

    TAccSmsaccount selectByPrimaryKey(Long companyId);

    int updateByPrimaryKeySelective(TAccSmsaccount record);

    int updateByPrimaryKey(TAccSmsaccount record);
}