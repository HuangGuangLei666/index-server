package com.pl.model.wx;

import java.util.Comparator;

/**
 * 比较器（根据id升序排序）
 * @author HuangGuangLei
 * @Date 2019/11/22
 */
public class ReverseSortTmall implements Comparator {
    public int compare(Object obj1,Object obj2) {
        TMall mall1 = (TMall)obj1;
        TMall mall2 = (TMall)obj2;
        return mall1.getId().compareTo(mall2.getId());
    }
}
