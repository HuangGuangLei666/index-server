package com.pl.indexserver.untils;

/**
 * 外呼任务的状态定义
 */
public enum CallTaskStatus {
    STATUS_START(1,"启动"),
    STATUS_PAUSE(2,"暂停"),
    STATUS_FINISH(3,"结束"),
    STATUS_NOTRELEASE(4,"未运行"),
    STATUS_INIT(5,"创建中");

    //状态码
    private int code;

    //描述
    private String desc;

    // 构造方法
    private CallTaskStatus(int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    public static String getDesc(int code) {
        for (CallTaskStatus c : CallTaskStatus.values()) {
            if (c.getCode() == code) {
                return c.desc;
            }
        }
        return null;
    }

    public  String getDesc() {
        return this.desc;
    }

    public int getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.code+"_"+this.desc;
    }
}
