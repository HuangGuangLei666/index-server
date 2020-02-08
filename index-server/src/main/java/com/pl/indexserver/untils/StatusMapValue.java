package com.pl.indexserver.untils;

import java.util.HashMap;
import java.util.Map;

public class StatusMapValue {

    private static Map<Integer,String> infoMap = new HashMap<>();

    static {
        infoMap.put(0,"未知");
        infoMap.put(1, "失败");
        infoMap.put(2,"成功");
    }


    public static String getStatusMapValue(Integer key){
        return infoMap.get(key);
    }
}
