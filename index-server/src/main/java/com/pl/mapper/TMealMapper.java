package com.pl.mapper;


import com.pl.model.wx.TMeal;

import java.util.List;

public interface TMealMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TMeal record);

    int insertSelective(TMeal record);

    TMeal selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TMeal record);

    int updateByPrimaryKey(TMeal record);

    List<TMeal> selectMealList();

    TMeal selectBygoodsId(Integer goodsId);

    TMeal selectByCodeMealId(String code);

    TMeal selectByTradeNo(String ordersSn);

    List<TMeal> selectAllMealList();

    TMeal selectByCodeAndMealId(String code);
}