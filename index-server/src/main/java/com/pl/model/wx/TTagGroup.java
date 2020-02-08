package com.pl.model.wx;

import java.util.Date;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/20 9:33
 * @Description 通讯录分类
 */
public class TTagGroup {
    private Integer id;
    private String name;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
