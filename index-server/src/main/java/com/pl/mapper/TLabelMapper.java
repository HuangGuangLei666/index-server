package com.pl.mapper;

import com.pl.model.wx.TLabel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TLabelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TLabel record);

    int insertSelective(TLabel record);

    TLabel selectByPrimaryKey(@Param("sysLabelId") Integer sysLabelId);

    int updateByPrimaryKeySelective(TLabel record);

    int updateByPrimaryKey(TLabel record);

    List<TLabel> selectLabelListByFathId(Integer fathId);

    List<TLabel> selectLabelByLevel(String level);

    List<TLabel> selectAllData();
}