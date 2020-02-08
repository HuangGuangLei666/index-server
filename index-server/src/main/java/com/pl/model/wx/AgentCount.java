package com.pl.model.wx;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 11:12
 * @Description
 */
public class AgentCount {
    private Integer payCount;
    private Integer actCount;
    private List<AgentsDto> retData;

    public Integer getPayCount() {
        return payCount;
    }

    public void setPayCount(Integer payCount) {
        this.payCount = payCount;
    }

    public Integer getActCount() {
        return actCount;
    }

    public void setActCount(Integer actCount) {
        this.actCount = actCount;
    }

    public List<AgentsDto> getRetData() {
        return retData;
    }

    public void setRetData(List<AgentsDto> retData) {
        this.retData = retData;
    }
}
