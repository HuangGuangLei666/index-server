package com.pl.mapper;


import com.pl.model.wx.OrderHistory;
import com.pl.model.wx.TOrder;

import java.util.List;

public interface TOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TOrder record);

    int insertSelective(TOrder record);

    TOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TOrder record);

    int updateByPrimaryKey(TOrder record);

    int addOrder(TOrder tOrder);

    void updateOrderStatus(String ordersSn);

    TOrder selectByTradeNo(String ordersSn);

    List<OrderHistory> selectOrderHistoryByOpenid(String openid);

    List<OrderHistory> selectCodeOrderByOpenid(String openid);

    List<TOrder> selectByOpenidAndStatus(String openid);

    List<TOrder> selectByUserIdAndStatus(Integer userId);

    List<TOrder> selectByUseridAndStatus(Integer userId);

    List<OrderHistory> selectOrderHistoryByUserId(Integer userId);

    List<OrderHistory> selectCodeOrderByUserId(Integer userId);

    int insertOrder(TOrder tOrder);

    void updOrderStatus(String ordersSn);

    TOrder selectByTradeno(String ordersSn);
}