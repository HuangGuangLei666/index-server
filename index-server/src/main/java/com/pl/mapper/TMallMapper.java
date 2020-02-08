package com.pl.mapper;


import com.pl.model.wx.TMall;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TMallMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TMall record);

    int insertSelective(TMall record);

    TMall selectByPrimaryKey(@Param("id")Integer id,@Param("userId")Integer userId);

    int updateByPrimaryKeySelective(TMall record);

    int updateByPrimaryKey(TMall record);

    List<TMall> selectAllData(@Param("userId") Integer userId);

    int insertlabel(TMall tMall);

    TMall selectByIdAndDefault(Integer id);

    int deleteLabel(@Param("id")Integer id, @Param("userId")Integer userId);

    TMall selectByIdAndUserid(@Param("fatherId")Integer fatherId, @Param("userId")Integer userId);

    List<TMall> selectAllDataDefaul();

    List<TMall> selectByFatherId(Integer id);

    void insertBaseData(Integer userId);

    List<TMall> selectByOpenid(String openid);

    List<TMall> selectAllByOpenid();

    List<TMall> selectAllByUserid();

    List<TMall> selectByUserid(Integer userId);
}