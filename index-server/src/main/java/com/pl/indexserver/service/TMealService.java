package com.pl.indexserver.service;

import com.pl.model.wx.TMeal;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/2
 */
public interface TMealService {
    List<TMeal> selectMealList();

    TMeal selectBygoodsId(Integer goodsId);

    TMeal selectByCodeMealId(String code);

    TMeal selectByTradeNo(String ordersSn);

    List<TMeal> mealListQry(Integer userId);
}
