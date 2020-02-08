package com.pl.indexserver.service;

import com.pl.model.wx.TLabel;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/28
 */
public interface TLabelService {
    TLabel selectLabelById(Integer sysLabelId);

    List<TLabel> selectLabelListByFathId(Integer fathId);

    List<TLabel> selectLabelByLevel(String level);

    List<TLabel> selectAllData();
}
