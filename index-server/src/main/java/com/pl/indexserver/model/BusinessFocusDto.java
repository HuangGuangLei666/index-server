package com.pl.indexserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * 智库关注点dto
 *
 * @Author bei.zhang
 * @Date 2018/8/8 18:12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessFocusDto implements Serializable {


    private Integer id;
    private String name;

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
}
