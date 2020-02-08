package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallTaskStatDto {

    private Long taskId;//任务id
    private String taskName;//任务名字
    private Long companyId;//公司id
    private Long businessId;//业务id
    private String beginTime;//开始时间
    private String endTime;//终止时间
    private Long dialogNum;//外呼总量
    private Long missNum;//未接总量
    private Long item2Num;//接听总量统计项
    private Long item13Num;//空号统计项
    private Long item15Num;//占线统计项
    private Long item17Num;//未接统计项
    private Long item18Num;//主键失败统计项
    private Long item20Num;//有意向统计项
    private Long item21Num;//考虑统计项
    private Long item23Num;//无意向统计项
    private Long item24Num;//在忙,统计项
    private Long item25Num;//A级意向统计项'
    private Long item26Num;//B级意向统计项
    private Long item27Num;//C级意向统计项
    private Long item28Num;//D级意向统计项
    private Long item29Num;//关注段A级意向
    private Long item30Num;//关注段B级意向
    private Long item31Num;//关注段C级意向
    private Long item32Num;//关注段D级意向
    private Long item33Num;//组合A级意向
    private Long item34Num;//组合B级意向
    private Long item35Num;//组合C级意向
    private Long item36Num;//组合D级意向
    private Long item37Num;//关注点有意向C及以上
    private Long item38Num;//关注段无意向
    private Long item39Num;//组合有意向C及以上
    private Long item40Num;//组合无意向
    private Long item41Num;//三种方式的最高无意向
    private Long item42Num;//三种方式的最高有意向
    private Long item43Num;//三种方式的最高A意向
    private Long item44Num;//三种方式的最高B意向
    private Long item45Num;//三种方式的最高C意向
    private Long item46Num;//三种方式的最高D意向
    private Long successNum;//成功数量

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getItem2Num() {
        return null==item2Num?0:item2Num;
    }

    public void setItem2Num(Long item2Num) {
        this.item2Num = item2Num;
    }

    public Long getItem13Num() {
        return null==item13Num?0:item13Num;
    }

    public void setItem13Num(Long item13Num) {
        this.item13Num = item13Num;
    }

    public Long getItem15Num() {
        return null==item15Num?0:item15Num;
    }

    public void setItem15Num(Long item15Num) {
        this.item15Num = item15Num;
    }

    public Long getItem17Num() {
        return null==item17Num?0:item17Num;
    }

    public void setItem17Num(Long item17Num) {
        this.item17Num = item17Num;
    }

    public Long getItem18Num() {
        return null==item18Num?0:item18Num;
    }

    public void setItem18Num(Long item18Num) {
        this.item18Num = item18Num;
    }

    public Long getItem20Num() {
        return null==item20Num?0:item20Num;
    }

    public void setItem20Num(Long item20Num) {
        this.item20Num = item20Num;
    }

    public Long getItem21Num() {
        return null==item21Num?0:item21Num;
    }

    public void setItem21Num(Long item21Num) {
        this.item21Num = item21Num;
    }

    public Long getItem23Num() {
        return null==item23Num?0:item23Num;
    }

    public void setItem23Num(Long item23Num) {
        this.item23Num = item23Num;
    }

    public Long getItem24Num() {
        return null==item24Num?0:item24Num;
    }

    public void setItem24Num(Long item24Num) {
        this.item24Num = item24Num;
    }

    public Long getItem25Num() {
        return null==item25Num?0:item25Num;
    }

    public void setItem25Num(Long item25Num) {
        this.item25Num = item25Num;
    }

    public Long getItem26Num() {
        return null==item26Num?0:item26Num;
    }

    public void setItem26Num(Long item26Num) {
        this.item26Num = item26Num;
    }

    public Long getItem27Num() {
        return null==item27Num?0:item27Num;
    }

    public void setItem27Num(Long item27Num) {
        this.item27Num = item27Num;
    }

    public Long getItem28Num() {
        return null==item28Num?0:item28Num;
    }

    public void setItem28Num(Long item28Num) {
        this.item28Num = item28Num;
    }

    public Long getDialogNum() {
        return null==dialogNum?0:dialogNum;
    }

    public void setDialogNum(Long dialogNum) {
        this.dialogNum = dialogNum;
    }

    public Long getMissNum() {
        return null==missNum?0:missNum;
    }

    public void setMissNum(Long missNum) {
        this.missNum = missNum;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getItem29Num() {
        return item29Num;
    }

    public void setItem29Num(Long item29Num) {
        this.item29Num = item29Num;
    }

    public Long getItem30Num() {
        return item30Num;
    }

    public void setItem30Num(Long item30Num) {
        this.item30Num = item30Num;
    }

    public Long getItem31Num() {
        return item31Num;
    }

    public void setItem31Num(Long item31Num) {
        this.item31Num = item31Num;
    }

    public Long getItem32Num() {
        return item32Num;
    }

    public void setItem32Num(Long item32Num) {
        this.item32Num = item32Num;
    }

    public Long getItem33Num() {
        return item33Num;
    }

    public void setItem33Num(Long item33Num) {
        this.item33Num = item33Num;
    }

    public Long getItem34Num() {
        return item34Num;
    }

    public void setItem34Num(Long item34Num) {
        this.item34Num = item34Num;
    }

    public Long getItem35Num() {
        return item35Num;
    }

    public void setItem35Num(Long item35Num) {
        this.item35Num = item35Num;
    }

    public Long getItem36Num() {
        return item36Num;
    }

    public void setItem36Num(Long item36Num) {
        this.item36Num = item36Num;
    }

    public Long getItem37Num() {
        return item37Num;
    }

    public void setItem37Num(Long item37Num) {
        this.item37Num = item37Num;
    }

    public Long getItem38Num() {
        return item38Num;
    }

    public void setItem38Num(Long item38Num) {
        this.item38Num = item38Num;
    }

    public Long getItem39Num() {
        return item39Num;
    }

    public void setItem39Num(Long item39Num) {
        this.item39Num = item39Num;
    }

    public Long getItem40Num() {
        return item40Num;
    }

    public void setItem40Num(Long item40Num) {
        this.item40Num = item40Num;
    }

    public Long getItem41Num() {
        return item41Num;
    }

    public void setItem41Num(Long item41Num) {
        this.item41Num = item41Num;
    }

    public Long getItem42Num() {
        return item42Num;
    }

    public void setItem42Num(Long item42Num) {
        this.item42Num = item42Num;
    }

    public Long getItem43Num() {
        return item43Num;
    }

    public void setItem43Num(Long item43Num) {
        this.item43Num = item43Num;
    }

    public Long getItem44Num() {
        return item44Num;
    }

    public void setItem44Num(Long item44Num) {
        this.item44Num = item44Num;
    }

    public Long getItem45Num() {
        return item45Num;
    }

    public void setItem45Num(Long item45Num) {
        this.item45Num = item45Num;
    }

    public Long getItem46Num() {
        return item46Num;
    }

    public void setItem46Num(Long item46Num) {
        this.item46Num = item46Num;
    }

    public Long getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Long successNum) {
        this.successNum = successNum;
    }
}
