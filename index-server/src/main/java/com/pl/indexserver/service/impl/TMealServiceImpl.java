package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TMealService;
import com.pl.mapper.TMealMapper;
import com.pl.mapper.TOrderMapper;
import com.pl.model.wx.TMeal;
import com.pl.model.wx.TOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/2
 */
@Service
public class TMealServiceImpl implements TMealService {

    @Autowired
    private TMealMapper tMealMapper;
    @Autowired
    private TOrderMapper tOrderMapper;

    @Override
    public List<TMeal> selectMealList() {
        return tMealMapper.selectMealList();
    }

    @Override
    public TMeal selectBygoodsId(Integer goodsId) {
        return tMealMapper.selectBygoodsId(goodsId);
    }

    @Override
    public TMeal selectByCodeMealId(String code) {
        return tMealMapper.selectByCodeMealId(code);
    }

    @Override
    public TMeal selectByTradeNo(String ordersSn) {
        return tMealMapper.selectByTradeNo(ordersSn);
    }

    @Override
    public List<TMeal> mealListQry(Integer userId) {
        List<TMeal> tMeals = new ArrayList<>();
        List<TMeal> tMealList = tMealMapper.selectAllMealList();

        //判断此用户是否新用户
        List<TOrder> tOrderList = tOrderMapper.selectByUseridAndStatus(userId);

        if (!CollectionUtils.isEmpty(tMealList)) {
            for (TMeal meal : tMealList) {
                TMeal tMeal = new TMeal();
                tMeal.setId(meal.getId());
                //如果是新用户首单开通包月vip只需0.1
                tMeal.setPrice((CollectionUtils.isEmpty(tOrderList) && meal.getId() == 1) ? "0.1" : meal.getPrice());
                tMeal.setType(meal.getType());
                tMeal.setCreateTime(new Date());
                tMeal.setName(meal.getName());
                tMeal.setOriginalPrice(meal.getOriginalPrice());
                tMeal.setUseDays(meal.getUseDays());
                tMeals.add(tMeal);
            }
            return tMeals;
        }
        return tMeals;
    }

    @Override
    public List<TMeal> selectMealsList() {
        return tMealMapper.selectMealsList();
    }
}
