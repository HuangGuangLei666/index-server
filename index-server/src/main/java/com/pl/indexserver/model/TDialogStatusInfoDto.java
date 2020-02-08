package com.pl.indexserver.model;

import java.util.HashMap;
import java.util.Map;

public class TDialogStatusInfoDto {

    private static Map<Integer,String> mapInfo(){
        Map<Integer,String> map1=new HashMap<>();
        map1.put(0,"未拨打");
        map1.put(1,"呼叫中");
        map1.put(2,"已接通");
        map1.put(12,"拒接");
        map1.put(13,"空号");
        map1.put(14,"关机");
        map1.put(15,"占线");
        map1.put(16,"停机");
        map1.put(17,"未接");
        //map1.put(18,"主叫失败");
        map1.put(20,"有意向");
        map1.put(21,"考虑");
        map1.put(22,"线路故障");
        map1.put(23,"无意向");
        map1.put(24,"在忙，晚点打");
        map1.put(25,"A级意向");
        map1.put(26,"B级意向");
        map1.put(27,"C级意向");
        map1.put(28,"D级意向");

        return map1;
    }

    public static Map<Integer, String> getMap() {
        return mapInfo();
    }
}
