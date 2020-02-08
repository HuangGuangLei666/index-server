package com.pl.indexserver.untils;

/**
 * 江西计生委报表状态
 *
 * @Author bei.zhang
 * @Date 2018/9/13 19:32
 */
public enum ReportStatisticsStatus {

    VISIT_SUCCESS("VISIT_SUCCESS", "回访成功"),
    VISIT_FAIL("VISIT_FAIL", "回访失败"),
    RECORD("RECORD", "接通"),
    WRONG_NUMBER("WRONG_NUMBER", "号码错误"),
    FAILED("FAILED", "呼叫失败"),
    NUM_NOT_FOUND("NUM_NOT_FOUND", "空号"),
    POWER_OFF("POWER_OFF", "关机"),
    BUSY("BUSY", "占线"),
    STOPED("STOPED", "停机"),
    NO_RESPONSE("NO_RESPONSE", "未接"),
    PAUSED("PAUSED", "主叫号码不可用"),
    TIMEOUT("TIMEOUT", "超时"),
    NOT_FOUND("NOT_FOUND", "线路故障"),
    PROCESS_ERROR("PROCESS_ERROR", "处理异常");

    ReportStatisticsStatus(String code, String name) {
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
        for (ReportStatisticsStatus reportStatisticsStatus : ReportStatisticsStatus.values()) {
            if (reportStatisticsStatus.getCode().equals(code)) {
                return reportStatisticsStatus.getName();
            }
        }
        return null;
    }

}
