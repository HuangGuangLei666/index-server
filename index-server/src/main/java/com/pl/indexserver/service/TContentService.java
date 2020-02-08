package com.pl.indexserver.service;

import com.pl.model.wx.TContent;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/24
 */
public interface TContentService {
    List<TContent> selectContentList(String cpName, String desc);

    TContent selectContentByBusinessId(Integer businessId);
}
