package com.pl.model.wx;

import java.util.Date;

/**
 * @author HuangGuangLei
 * @Date 2019/10/20
 */
public class OperationReDto {
    private Integer userId;
    private Date createTime;
    private Integer operationId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
    }
}
