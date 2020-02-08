package com.pl.indexserver.untils;

/**
 * 操作类型
 */
public enum OperateType {
    CREATE(1, "创建"),
    MODIFY(2, "修改"),
    DELETE(3, "删除");

    //状态码
    private int code;

    //描述
    private String desc;

    // 构造方法
    private OperateType(int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    public static String getDesc(int code) {
        for (OperateType c : OperateType.values()) {
            if (c.getCode() == code) {
                return c.desc;
            }
        }
        return null;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.code + "_" + this.desc;
    }
}
