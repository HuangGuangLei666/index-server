package com.pl.indexserver.untils;

/**
 * 计划生育状态
 *
 * @Author bei.zhang
 * @Date 2018/9/13 19:32
 */
public enum FamilyPlanningStatus {

    SUCCESS("SUCCESS", "回访成功"),
    NOT_COOPERATING("NOT_COOPERATING", "不配合"),
    FAIL("FAIL", "回访失败"),
    HANGUP("HANGUP", "直接挂机"),
    STOPED("STOPED", "停机"),
    POWER_OFF("POWER_OFF", "关机"),
    EMPTY("EMPTY", "空号"),
    NO_HEARD("NO_HEARD", "无人接听"),
    BUSY("BUSY", "暂时无法接听");

    FamilyPlanningStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String converToName(String code) {
        if (null == code || code.trim().length() <= 0) {
            return null;
        }
        for (FamilyPlanningStatus familyPlanningStatus : FamilyPlanningStatus.values()) {
            if (familyPlanningStatus.getCode().equals(code)) {
                return familyPlanningStatus.getName();
            }
        }
        return null;
    }

}
