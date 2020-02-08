package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TOrderService;
import com.pl.mapper.TOrderMapper;
import com.pl.model.wx.OrderHistory;
import com.pl.model.wx.OrderHistoryResp;
import com.pl.model.wx.ReverseSort;
import com.pl.model.wx.TOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/10
 */
@Service
public class TOrderServiceImpl implements TOrderService {

    private static final Logger logger = LoggerFactory.getLogger(TOrderServiceImpl.class);

    @Autowired
    private TOrderMapper tOrderMapper;

    @Override
    public int addOrder(TOrder tOrder) {
        return tOrderMapper.addOrder(tOrder);
    }

    @Override
    public TOrder selectById(Integer goodsId) {
        return tOrderMapper.selectByPrimaryKey(goodsId);
    }

    @Override
    public void updateOrderStatus(String ordersSn) {
        tOrderMapper.updateOrderStatus(ordersSn);
    }

    @Override
    public TOrder selectByTradeNo(String ordersSn) {
        return tOrderMapper.selectByTradeNo(ordersSn);
    }

    @Override
    public List<OrderHistory> selectOrderHistoryByOpenid(String openid) {
        return tOrderMapper.selectOrderHistoryByOpenid(openid);
    }

    @Override
    public List<OrderHistory> selectCodeOrderByOpenid(String openid) {
        return tOrderMapper.selectCodeOrderByOpenid(openid);
    }

    @Override
    public List<TOrder> selectByOpenidAndStatus(String openid) {
        return tOrderMapper.selectByOpenidAndStatus(openid);
    }

    @Override
    public List<TOrder> selectByUserIdAndStatus(Integer userId) {
        return tOrderMapper.selectByUserIdAndStatus(userId);
    }

    @Override
    public OrderHistoryResp orderHistoryQry(Integer userId) {
        OrderHistoryResp resp = new OrderHistoryResp();
        List<OrderHistory> orderHistories = new ArrayList<>();
        List<OrderHistory> codeOrders = new ArrayList<>();
        //无激活码订单
        List<OrderHistory> orderHistoryList = tOrderMapper.selectOrderHistoryByUserId(userId);
        logger.info("========orderHistoryList={}==========", orderHistoryList.size());
        if (!StringUtils.isEmpty(orderHistoryList)) {
            for (OrderHistory order : orderHistoryList) {
                OrderHistory orderHistory1 = new OrderHistory();
                orderHistory1.setOpenid(order.getOpenid());
                orderHistory1.setMealName(order.getMealName());
                orderHistory1.setCreateTime(order.getCreateTime());
                orderHistory1.setNumber(order.getNumber());
                orderHistory1.setPayMoney(order.getPayMoney());
                orderHistory1.setPayTime(order.getPayTime());
                orderHistory1.setPrice(order.getPrice());
                orderHistory1.setStatus(order.getStatus());
                orderHistory1.setTradeNo(order.getTradeNo());
                orderHistory1.setType(order.getType());

                orderHistories.add(orderHistory1);
            }
        }
        logger.info("========orderHistories={}==========", orderHistories.size());

        //激活码订单
        List<OrderHistory> codeOrderList = tOrderMapper.selectCodeOrderByUserId(userId);
        logger.info("========codeOrderList={}==========", codeOrderList.size());
        if (!StringUtils.isEmpty(codeOrderList)) {
            for (OrderHistory order : codeOrderList) {
                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setOpenid(order.getOpenid());
                orderHistory.setMealName(order.getMealName());
                orderHistory.setCreateTime(order.getCreateTime());
                orderHistory.setNumber(order.getNumber());
                orderHistory.setPayMoney(order.getPayMoney());
                orderHistory.setPayTime(order.getPayTime());
                orderHistory.setPrice(order.getPrice());
                orderHistory.setStatus(order.getStatus());
                orderHistory.setTradeNo(order.getTradeNo());
                orderHistory.setCode(order.getCode());
                orderHistory.setType(order.getType());

                codeOrders.add(orderHistory);
            }
        }
        //两个集合 求并集
        orderHistories.addAll(codeOrders);
        //用比较器实现 根据createtime倒序排序
        Collections.sort(orderHistories, new ReverseSort());
        logger.info("========orderHistories={}==========", orderHistories.size());

        resp.setRetCode(0);
        resp.setRetDesc("历史订单如下（包含激活码）");
        resp.setRetData(orderHistories);
        return resp;
    }
}
