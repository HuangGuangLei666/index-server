package com.pl.model.wx;

import java.util.Date;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/27 17:00
 * @Description
 */
public class InterceptSta {
    private Integer id;
    private String callerphone;
    private String calleephone;
    private Integer type;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCallerphone() {
        return callerphone;
    }

    public void setCallerphone(String callerphone) {
        this.callerphone = callerphone;
    }

    public String getCalleephone() {
        return calleephone;
    }

    public void setCalleephone(String calleephone) {
        this.calleephone = calleephone;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
