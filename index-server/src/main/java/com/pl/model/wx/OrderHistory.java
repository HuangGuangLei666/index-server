package com.pl.model.wx;


import java.util.Date;

/**
 * @author HuangGuangLei
 * @Date 2019/11/16
 */
public class OrderHistory {
    private String openid;
    private String mealName;
    private String price;
    private Integer number;
    private String payMoney;
    private Integer status;
    private Date createTime;
    private Date payTime;
    private String tradeNo;
    private String code;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "OrderHistory{" +
                "openid='" + openid + '\'' +
                ", mealName='" + mealName + '\'' +
                ", price='" + price + '\'' +
                ", number=" + number +
                ", payMoney='" + payMoney + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", payTime=" + payTime +
                ", tradeNo='" + tradeNo + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
