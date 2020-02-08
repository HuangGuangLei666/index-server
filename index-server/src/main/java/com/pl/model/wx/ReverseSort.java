package com.pl.model.wx;

import java.util.Comparator;

/**
 * @author HuangGuangLei
 * @Date 2019/11/22
 */
public class ReverseSort implements Comparator {
    public int compare(Object obj1,Object obj2) {
        OrderHistory OrderHistory1 = (OrderHistory)obj1;
        OrderHistory OrderHistory2 = (OrderHistory)obj2;
        return -OrderHistory1.getCreateTime().compareTo(OrderHistory2.getCreateTime());
    }
}
