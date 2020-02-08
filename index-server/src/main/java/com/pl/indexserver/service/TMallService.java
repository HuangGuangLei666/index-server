package com.pl.indexserver.service;

import com.pl.model.wx.TMall;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/4
 */
public interface TMallService {
    List<TMall> selectAllData(Integer userId);

    int insertlabel(TMall tMall);

    TMall selectById(Integer id,Integer userId);

    TMall selectByIdAndDefault(Integer id);

    int deleteLabel(Integer id, Integer userId);

    TMall selectByIdAndUserid(Integer fatherId, Integer userId);

    List<TMall> selectAllDataDefaul();

    List<TMall> selectByFatherId(Integer id);

    void insertBaseData(Integer id);

    List<TMall> selectByOpenid(String openid);

    List<TMall> selectAllByOpenid();

    List<TMall> userIntrestingQry(Integer userId);
}
